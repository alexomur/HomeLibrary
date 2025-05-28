package com.example.homelibrary;

import android.net.Uri;

import com.example.homelibrary.models.Author;
import com.example.homelibrary.models.Book;
import com.example.homelibrary.models.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Manages all database and storage operations for the HomeLibrary application.
 */
public class DBManager {
    private static final String USERS = "users";
    private static final String AUTHORS = "authors";
    private static final String BOOKS = "books";

    private static DBManager instance;

    private FirebaseAuth auth;
    private DatabaseReference db;
    private FirebaseStorage storage;

    /**
     * Returns the single instance of DBManager.
     */
    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    /**
     * Initializes Firebase Auth, Realtime Database, and Storage instances.
     */
    private DBManager() {
        auth = FirebaseAuth.getInstance();
        db = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Registers a new user with email and password.
     * Saves basic user info to the database.
     *
     * @param email    User's email address
     * @param password User's chosen password
     * @return Task result of the registration operation
     */
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

    /**
     * Logs in an existing user using email and password.
     *
     * @param email    User's email address
     * @param password User's password
     * @return Task result of the login operation
     */
    public Task<AuthResult> login(String email, String password) {
        return auth.signInWithEmailAndPassword(email, password);
    }

    /**
     * Logs in or registers a user using OAuth credentials (e.g., Google sign-in).
     *
     * @param credential Auth credential from OAuth provider
     * @return Task result of the OAuth login operation
     */
    public Task<AuthResult> oauthLogin(AuthCredential credential) {
        return auth.signInWithCredential(credential);
    }

    /**
     * Saves a new author record to the database.
     *
     * @param author Author model to store
     */
    public void saveAuthor(Author author) {
        db.child(AUTHORS).child(author.id).setValue(author);
    }

    /**
     * Uploads a book file to storage, sets its download URL, and saves book metadata.
     *
     * @param book    Book metadata (id and storagePath must be set)
     * @param fileBytes Byte array of the book file
     * @return UploadTask for monitoring progress
     */
    public UploadTask uploadBook(Book book, byte[] fileBytes) {
        StorageReference ref = storage.getReference().child(book.storagePath);
        UploadTask task = ref.putBytes(fileBytes);
        task.continueWithTask(t -> ref.getDownloadUrl())
                .addOnSuccessListener(uri ->
                    db.child(BOOKS).child(book.id).setValue(book)
                );
        return task;
    }

    /**
     * Downloads a book file from storage, saves it to local file, and records the download.
     *
     * @param storagePath Path of the book in Firebase Storage
     * @param localFile   Local destination File where the book will be saved
     * @param userId      ID of the user downloading the book
     * @param bookId      ID of the book being downloaded
     * @return FileDownloadTask for monitoring progress
     */
    public FileDownloadTask downloadBook(String storagePath, File localFile, String userId, String bookId) {
        StorageReference ref = storage.getReference().child(storagePath);
        FileDownloadTask task = ref.getFile(localFile);
        task.addOnSuccessListener(snapshot -> {
            recordDownload(userId, bookId);
        });
        return task;
    }

    /**
     * Records that a user has downloaded a specific book.
     */
    public void recordDownload(String userId, String bookId) {
        db.child(USERS)
                .child(userId)
                .child("downloadedBooks")
                .child(bookId)
                .setValue(true);
    }
}