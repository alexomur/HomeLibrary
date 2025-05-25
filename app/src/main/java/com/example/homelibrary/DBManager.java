package com.example.homelibrary;

import com.example.homelibrary.models.Author;
import com.example.homelibrary.models.Book;
import com.example.homelibrary.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class DBManager {
    private static final String USERS = "users";
    private static final String AUTHORS = "authors";
    private static final String BOOKS = "books";

    private FirebaseAuth auth;
    private DatabaseReference db;
    private FirebaseStorage storage;

    public DBManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
    }

    public Task<AuthResult> register(final String email, final String password) {
        return auth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(res -> {
                    FirebaseUser u = res.getUser();
                    if (u != null) {
                        User user = new User(u.getUid(), email);
                        db.child(USERS).child(u.getUid()).setValue(user);
                    }
                });
    }

    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    public Task<AuthResult> oauthLogin(AuthCredential credential) {
        return auth.signInWithCredential(credential);
    }

    public void saveAuthor(Author author) {
        db.child(AUTHORS).child(author.id).setValue(author);
    }

    public void saveBook(Book book, byte[] fileBytes) {
        String path = "books/" + book.id + ".pdf";
        storage.getReference(path)
                .putBytes(fileBytes)
                .continueWithTask(task -> storage.getReference(path).getDownloadUrl())
                .addOnSuccessListener(uri -> {
                    book.fileUrl = uri.toString();
                    db.child(BOOKS).child(book.id).setValue(book);
                });
    }

    public void recordDownload(String userId, String bookId) {
        db.child(USERS)
                .child(userId)
                .child("downloadedBooks")
                .child(bookId)
                .setValue(true);
    }

    public Task<android.net.Uri> getBookDownloadUrl(String bookId) {
        return storage.getReference("books/" + bookId + ".pdf").getDownloadUrl();
    }
}
