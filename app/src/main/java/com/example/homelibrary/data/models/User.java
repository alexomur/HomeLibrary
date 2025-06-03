package com.example.homelibrary.data.models;

import java.util.List;

/**
 * Represents a user in the HomeLibrary application.
 * This model is used for interactions with the database.
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
     * List of book IDs that the user has downloaded.
     */
    public List<String> downloadedBooks;

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
        this.uid = uid;
        this.email = email;
    }
}