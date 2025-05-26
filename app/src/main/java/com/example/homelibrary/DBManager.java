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

/**
 * Manages all database operations for the HomeLibrary application.
 * Handles user authentication, author/book storage, and file uploads.
 */
public class DBManager {
    private static final String USERS = "users";
    private static final String AUTHORS = "authors";
    private static final String BOOKS = "books";

    private FirebaseAuth auth;
    private DatabaseReference db;
    private FirebaseStorage storage;

    /**
     * Initializes Firebase Auth, Realtime Database, and Storage instances.
     */
    public DBManager() {
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
     * Uploads book file to storage, retrieves its URL, and saves book record.
     *
     * @param book      Book model to store
     * @param fileBytes Byte array of the book file (e.g., PDF data)
     */
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

    /**
     * Records that a user has downloaded a specific book.
     *
     * @param userId ID of the user
     * @param bookId ID of the downloaded book
     */
    public void recordDownload(String userId, String bookId) {
        db.child(USERS)
                .child(userId)
                .child("downloadedBooks")
                .child(bookId)
                .setValue(true);
    }

    /**
     * Retrieves the download URL for a stored book file.
     *
     * @param bookId ID of the book
     * @return Task that resolves to the file's download URI
     */
    public Task<android.net.Uri> getBookDownloadUrl(String bookId) {
        return storage.getReference("books/" + bookId + ".pdf").getDownloadUrl();
    }
}
