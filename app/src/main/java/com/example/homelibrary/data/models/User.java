package com.example.homelibrary.data.models;

/**
 * Represents a user in the HomeLibrary application.
 */
public class User {
    /**
     * Unique identifier of the user (provided by Firebase Auth).
     */
    public String uid;

    /**
     * User's email address used for login.
     */
    public String email;

    /**
     * Display nickname.
     */
    public String nickname;

    /** Remote avatar url (may be null). */
    public String avatarUrl;

    /**
     * Default constructor required for Firebase deserialization.
     */
    public User() {}

    /**
     * Creates a new User instance.
     *
     * @param uid   Unique user ID
     * @param email User's email address
     */
    public User(String uid, String email) {
        this(uid, email, null, null);
    }

    /** Full ctor. */
    public User(String uid, String email, String nickname, String avatarUrl) {
        this.uid = uid;
        this.email = email;
        this.nickname = (nickname == null || nickname.isEmpty())
                ? (email != null ? email.split("@")[0] : "user")
                : nickname;
        this.avatarUrl = avatarUrl;
    }
}
