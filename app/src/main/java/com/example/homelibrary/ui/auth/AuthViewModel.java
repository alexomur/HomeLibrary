package com.example.homelibrary.ui.auth;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.homelibrary.data.AuthManager;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;

/**
 * ViewModel for handling authentication logic in LoginFragment, RegisterFragment, and GoogleSignInFragment.
 */
public class AuthViewModel extends ViewModel {

    /**
     * Represents the current state of an authentication operation.
     */
    public enum Status {
        IDLE,       // No operation in progress
        LOADING,    // Authentication/registration in progress
        SUCCESS,    // Operation succeeded (login, register, or OAuth)
        ERROR       // Operation failed
    }

    private final AuthManager authManager;
    private final MutableLiveData<Status> statusLiveData = new MutableLiveData<>(Status.IDLE);
    private final MutableLiveData<String> errorMessageLiveData = new MutableLiveData<>(null);

    /**
     * Initializes AuthManager instance.
     */
    public AuthViewModel() {
        authManager = AuthManager.getInstance();
    }

    /**
     * Returns LiveData for authentication status.
     *
     * @return LiveData representing current Status
     */
    public LiveData<Status> getStatus() {
        return statusLiveData;
    }

    /**
     * Returns LiveData for error messages.
     *
     * @return LiveData containing the latest error message, or null if none
     */
    public LiveData<String> getErrorMessage() {
        return errorMessageLiveData;
    }

    /**
     * Attempts to sign in a user with the provided email and password.
     * Updates statusLiveData to LOADING, then SUCCESS or ERROR.
     *
     * @param email    user’s email address
     * @param password user’s password
     */
    public void login(String email, String password) {
        statusLiveData.setValue(Status.LOADING);

        authManager.login(email, password)
                .addOnSuccessListener(authResult -> {
                    statusLiveData.setValue(Status.SUCCESS);
                    errorMessageLiveData.setValue(null);
                })
                .addOnFailureListener(e -> {
                    statusLiveData.setValue(Status.ERROR);
                    errorMessageLiveData.setValue(e.getMessage());
                });
    }

    /**
     * Attempts to register a new user with the provided email and password.
     * On success, the new user’s profile is stored by AuthManager.
     * Updates statusLiveData to LOADING, then SUCCESS or ERROR.
     *
     * @param email    new user’s email address
     * @param password new user’s password
     */
    public void register(String email, String password) {
        statusLiveData.setValue(Status.LOADING);

        authManager.register(email, password)
                .addOnSuccessListener(authResult -> {
                    statusLiveData.setValue(Status.SUCCESS);
                    errorMessageLiveData.setValue(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("AuthViewModel", "Registration failed", e);
                    statusLiveData.setValue(Status.ERROR);
                    errorMessageLiveData.setValue(e.getMessage());
                });
    }

    /**
     * Attempts OAuth login (e.g., Google) with the given AuthCredential.
     * Updates statusLiveData to LOADING, then SUCCESS or ERROR.
     *
     * @param credential AuthCredential obtained from GoogleSignInAccount
     */
    public void oauthLogin(AuthCredential credential) {
        statusLiveData.setValue(Status.LOADING);

        authManager.oauthLogin(credential)
                .addOnSuccessListener(authResult -> {
                    statusLiveData.setValue(Status.SUCCESS);
                    errorMessageLiveData.setValue(null);
                })
                .addOnFailureListener(e -> {
                    Log.e("AuthViewModel", "OAuth login failed", e);
                    statusLiveData.setValue(Status.ERROR);
                    errorMessageLiveData.setValue(e.getMessage());
                });
    }

    /**
     * Sends a password reset email to the specified address.
     * Updates statusLiveData to LOADING, then SUCCESS or ERROR.
     *
     * @param email target email for password reset link
     */
    public void sendPasswordReset(String email) {
        statusLiveData.setValue(Status.LOADING);

        authManager.sendPasswordResetEmail(email)
                .addOnSuccessListener(aVoid -> {
                    statusLiveData.setValue(Status.SUCCESS);
                    errorMessageLiveData.setValue(null);
                })
                .addOnFailureListener(e -> {
                    statusLiveData.setValue(Status.ERROR);
                    errorMessageLiveData.setValue(e.getMessage());
                });
    }

    /**
     * Signs out the current user and resets status and error LiveData.
     */
    public void logout() {
        authManager.logout();
        statusLiveData.setValue(Status.IDLE);
        errorMessageLiveData.setValue(null);
    }

    /**
     * Checks if a user is already signed in at fragment/activity start.
     *
     * @return true if a FirebaseUser is currently signed in, false otherwise
     */
    public boolean isUserLoggedIn() {
        return authManager.getCurrentUser() != null;
    }
}
