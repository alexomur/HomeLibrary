package com.example.homelibrary.models;

import java.util.List;

public class Book {
    public String id;
    public String title;
    public String description;
    public String genre;
    public List<String> authorIds;
    public String fileUrl;

    public Book() {}

    public Book(String id, String title, String description, String genre,
                List<String> authorIds, String fileUrl) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.genre = genre;
        this.authorIds = authorIds;
        this.fileUrl = fileUrl;
    }
}
