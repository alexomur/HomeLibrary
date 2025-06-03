package com.example.homelibrary.data;

import com.example.homelibrary.data.models.Author;
import com.example.homelibrary.data.models.Book;
import com.example.homelibrary.data.models.User;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Manages low-level interactions with Firebase Realtime Database and Storage.
 * Handles creation and updates of User, Author, and Book records,
 * as well as uploading and downloading book files.
 */
public class DBManager {
    private static final String USERS   = "users";
    private static final String AUTHORS = "authors";
    private static final String BOOKS   = "books";

    private static DBManager instance;

    private final DatabaseReference db;
    private final FirebaseStorage storage;

    private DBManager() {
        db = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
    }

    /**
     * Returns the singleton instance of DBManager.
     *
     * @return shared DBManager instance
     */
    public static synchronized DBManager getInstance() {
        if (instance == null) {
            instance = new DBManager();
        }
        return instance;
    }

    // ====== User operations ======

    /**
     * Saves a User object under the "users/{uid}" path.
     *
     * @param user a User model containing uid, email, and other profile data
     */
    public void saveUser(User user) {
        db.child(USERS).child(user.uid).setValue(user);
    }

    /**
     * Updates a single field for an existing user.
     * For example, updating display name or downloadedBooks list.
     *
     * @param userId identifier of the user to update
     * @param key    the child key under "users/{userId}" to update
     * @param value  new value to set at that key
     */
    public void updateUserField(String userId, String key, Object value) {
        db.child(USERS).child(userId).child(key).setValue(value);
    }

    // ====== Author operations ======

    /**
     * Saves an Author object under the "authors/{authorId}" path.
     *
     * @param author an Author model containing id, fullName, biography, and bookIds
     */
    public void saveAuthor(Author author) {
        db.child(AUTHORS).child(author.id).setValue(author);
    }

    /**
     * Updates a single field for an existing author.
     *
     * @param authorId identifier of the author to update
     * @param key      the child key under "authors/{authorId}" to update
     * @param value    new value to set at that key
     */
    public void updateAuthorField(String authorId, String key, Object value) {
        db.child(AUTHORS).child(authorId).child(key).setValue(value);
    }

    // ====== Book operations and Storage ======

    /**
     * Saves book metadata under the "books/{bookId}" path.
     * File bytes must be uploaded separately via {@link #uploadBookFile(Book, byte[])}.
     *
     * @param book a Book model containing id, title, description, genre, authorIds, and storagePath
     */
    public void saveBookMetadata(Book book) {
        db.child(BOOKS).child(book.id).setValue(book);
    }

    /**
     * Uploads raw book bytes to Firebase Storage. Once a download URL is obtained,
     * the method saves the Book metadata in Realtime Database.
     *
     * @param book      Book model whose storagePath field specifies the Storage location
     * @param fileBytes byte array of the book file to upload
     * @return UploadTask to monitor upload progress
     */
    public UploadTask uploadBookFile(Book book, byte[] fileBytes) {
        StorageReference ref = storage.getReference().child(book.storagePath);
        UploadTask task = ref.putBytes(fileBytes);
        task.continueWithTask(taskSnapshot -> ref.getDownloadUrl())
                .addOnSuccessListener(uri -> saveBookMetadata(book));
        return task;
    }

    /**
     * Downloads a book file from Firebase Storage to a local file.
     * Upon successful download, records the download under the user's profile.
     *
     * @param storagePath path of the file in Firebase Storage (e.g., "books/{id}.pdf")
     * @param localFile   destination File on the device
     * @param userId      identifier of the user who is downloading
     * @param bookId      identifier of the book being downloaded
     * @return FileDownloadTask to monitor download progress
     */
    public FileDownloadTask downloadBookFile(String storagePath, File localFile, String userId, String bookId) {
        StorageReference ref = storage.getReference().child(storagePath);
        FileDownloadTask task = ref.getFile(localFile);
        task.addOnSuccessListener(snapshot -> recordDownload(userId, bookId));
        return task;
    }

    /**
     * Records in Realtime Database that a user has downloaded a specific book.
     * Creates a true value at "users/{userId}/downloadedBooks/{bookId}".
     *
     * @param userId identifier of the user
     * @param bookId identifier of the book that was downloaded
     */
    private void recordDownload(String userId, String bookId) {
        db.child(USERS)
                .child(userId)
                .child("downloadedBooks")
                .child(bookId)
                .setValue(true);
    }
}
