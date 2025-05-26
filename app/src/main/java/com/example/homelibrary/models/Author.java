package com.example.homelibrary.models;

import androidx.annotation.Nullable;
import java.util.List;

/**
 * Represents an author in the HomeLibrary application.
 * This model is used for interactions with the database.
 */
public class Author {
    /**
     * Unique identifier for the author.
     */
    public String id;

    /**
     * Full name of the author.
     */
    public String fullName;

    /**
     * Short biography of the author.
     */
    public String biography;

    /**
     * List of book IDs written by the author.
     */
    public List<String> bookIds;

    /**
     * Default constructor required for Firebase deserialization.
     */
    public Author() {}

    /**
     * Creates a new Author instance.
     *
     * @param id         Unique author ID
     * @param fullName   Full name of the author
     * @param biography  Author biography
     * @param bookIds    List of related book IDs
     */
    public Author(String id, String fullName, String biography, List<String> bookIds) {
        this.id = id;
        this.fullName = fullName;
        this.biography = biography;
        this.bookIds = bookIds;
    }
}