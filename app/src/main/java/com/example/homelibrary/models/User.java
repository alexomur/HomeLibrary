package com.example.homelibrary.models;

import java.util.List;

public class User {
    public String uid;
    public String email;
    public List<String> downloadedBookIds;

    public User() {}

    public User(String uid, String login) {
        this.uid = uid;
        this.email = login;
    }
}
