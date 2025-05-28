package com.example.homelibrary.models;

import java.util.List;

/**
 * Represents a book in the HomeLibrary application.
 * This model is used for interactions with the database and storage.
 */
public class Book {
    /**
     * Unique identifier for the book.
     */
    public String id;

    /**
     * Title of the book.
     */
    public String title;

    /**
     * Short description or summary of the book.
     */
    public String description;

    /**
     * Genre or category of the book.
     */
    public String genre;

    /**
     * List of author IDs associated with this book.
     */
    public List<String> authorIds;

    /**
     * Path in Firebase Storage where the book is stored (e.g. "books/{id}.pdf").
     */
    public String storagePath;

    /**
     * Default constructor required for Firebase deserialization.
     */
    public Book() {}

    /**
     * Creates a new Book instance.
     *
     * @param id          Unique book ID
     * @param title       Book title
     * @param description Book summary
     * @param genre       Book genre
     * @param authorIds   List of related author IDs
     * @param storagePath Storage path of the book file in Firebase Storage
     */
    public Book(String id, String title, String description, String genre,
                List<String> authorIds, String storagePath) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.authorIds = authorIds;
        this.storagePath = storagePath;
    }
}