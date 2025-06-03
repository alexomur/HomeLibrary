package com.example.homelibrary.data;

import com.example.homelibrary.data.models.User;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.android.gms.tasks.Task;

/**
 * Provides a higher-level API over FirebaseAuth and DBManager.
 */
public class AuthManager {

    private static AuthManager instance;

    private final FirebaseAuth firebaseAuth;
    private final DBManager dbManager;

    private AuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        dbManager = DBManager.getInstance();
    }

    /**
     * Returns the singleton instance of AuthManager.
     *
     * @return shared AuthManager instance
     */
    public static synchronized AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }

    // ======================= Registration =======================

    /**
     * Registers a new user with email and password in FirebaseAuth.
     * On successful registration, saves a minimal User profile (uid + email)
     * in Realtime Database via DBManager.
     *
     * @param email    new user's email address
     * @param password new user's password
     * @return Task<AuthResult> for attaching success/failure listeners
     */
    public Task<AuthResult> register(final String email, final String password) {
        return firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fbUser = authResult.getUser();
                    if (fbUser != null) {
                        String uid = fbUser.getUid();
                        User userModel = new User(uid, email);
                        dbManager.saveUser(userModel);
                    }
                });
    }

    // ======================= Login =======================

    /**
     * Signs in an existing user with email and password.
     *
     * @param email    user's email address
     * @param password user's password
     * @return Task<AuthResult> for attaching success/failure listeners
     */
    public Task<AuthResult> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    // ======================= OAuth Login =======================

    /**
     * Signs in (or registers) a user with an AuthCredential (e.g., Google).
     * On success, if first-time Google user, saves minimal profile to DB.
     *
     * @param credential AuthCredential from GoogleSignIn
     * @return Task<AuthResult> for attaching success/failure listeners
     */
    public Task<AuthResult> oauthLogin(AuthCredential credential) {
        return firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser fbUser = authResult.getUser();
                    if (fbUser != null) {
                        String uid = fbUser.getUid();
                        String email = fbUser.getEmail() != null ? fbUser.getEmail() : "";
                        // Save minimal profile if it doesn't exist yet
                        dbManager.updateUserField(uid, "email", email);
                    }
                });
    }

    // ======================= Password Reset =======================

    /**
     * Sends a password reset email to the specified address.
     *
     * @param email target email for password reset link
     * @return Task<Void> for attaching success/failure listeners
     */
    public Task<Void> sendPasswordResetEmail(String email) {
        return firebaseAuth.sendPasswordResetEmail(email);
    }

    // ======================= Logout =======================

    /**
     * Signs out the current user, clearing the FirebaseAuth session.
     */
    public void logout() {
        firebaseAuth.signOut();
    }

    // ======================= Current User =======================

    /**
     * Returns the currently signed-in FirebaseUser, or null if no user is logged in.
     *
     * @return current FirebaseUser or null
     */
    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }
}
