package com.example.homelibrary.data;

import com.example.homelibrary.data.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Provides high-level API over FirebaseAuth and Realtime DB.
 */
public class AuthManager {

    private static AuthManager instance;

    private final FirebaseAuth firebaseAuth;
    private final DBManager dbManager;

    private AuthManager() {
        firebaseAuth = FirebaseAuth.getInstance();
        dbManager = DBManager.getInstance();
    }

    /** Singleton accessor. */
    public static synchronized AuthManager getInstance() {
        if (instance == null) instance = new AuthManager();
        return instance;
    }

    // ======================= Registration =======================

    public Task<AuthResult> register(String email, String password) {
        return firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(r -> {
                    FirebaseUser f = r.getUser();
                    if (f != null) dbManager.saveUser(new User(f.getUid(), email));
                });
    }

    // ======================= Login =======================

    public Task<AuthResult> login(String email, String password) {
        return firebaseAuth.signInWithEmailAndPassword(email, password);
    }

    // ======================= OAuth Login =======================

    public Task<AuthResult> oauthLogin(AuthCredential credential) {
        return firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(r -> {
                    FirebaseUser f = r.getUser();
                    if (f != null && f.getEmail() != null) {
                        dbManager.updateUserField(f.getUid(), "email", f.getEmail());
                    }
                });
    }

    // ======================= Password Reset =======================

    public Task<Void> sendPasswordResetEmail(String email) {
        return firebaseAuth.sendPasswordResetEmail(email);
    }

    // ======================= Logout =======================

    public void logout() {
        firebaseAuth.signOut();
    }

    // ======================= Current User =======================

    public FirebaseUser getCurrentUser() {
        return firebaseAuth.getCurrentUser();
    }

    // ======================= Profile update helpers =======================

    public Task<Void> updateNickname(String newNick) {
        String uid = getCurrentUser().getUid();
        return dbManager.updateUserField(uid, "nickname", newNick);
    }

    public Task<Void> updateAvatar(String url) {
        String uid = getCurrentUser().getUid();
        return dbManager.updateUserField(uid, "avatarUrl", url);
    }

    public Task<Void> updateEmail(String newEmail) {
        FirebaseUser user = getCurrentUser();
        if (user == null) return null;

        return user.verifyBeforeUpdateEmail(newEmail)
                .addOnSuccessListener(v ->
                        dbManager.updateUserField(
                                user.getUid(), "email", newEmail));
    }
}
