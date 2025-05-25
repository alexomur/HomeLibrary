package com.example.homelibrary.models;

import androidx.annotation.Nullable;

import java.util.List;

public class Author {
    public String id;
    public String fullName;
    public String biography;
    public List<String> bookIds;

    public Author() {}

    public Author(String id, String fullName, String biography, List<String> bookIds) {
        this.id = id;
        this.fullName = fullName;
        this.biography = biography;
        this.bookIds = bookIds;
    }
}
