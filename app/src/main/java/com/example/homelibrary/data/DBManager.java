package com.example.homelibrary.data;

import com.example.homelibrary.data.models.Author;
import com.example.homelibrary.data.models.Book;
import com.example.homelibrary.data.models.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/**
 * Manages low-level interactions with Firebase Realtime Database.
 */
public class DBManager {

    private static DBManager instance;

    private static final String USERS = "users";
    private static final String AUTHORS = "authors";
    private static final String BOOKS = "books";

    private final DatabaseReference db;

    private DBManager() {
        db = FirebaseDatabase.getInstance().getReference();
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
     * Updates given field of User object under the "users/{uid}" path.
     */
    public Task<Void> updateUserField(String userId, String key, Object value) {
        return db.child(USERS).child(userId).child(key).setValue(value);
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

    public Task<Void> updateAuthorField(String authorId, String key, Object value) {
        return db.child(AUTHORS).child(authorId).child(key).setValue(value);
    }

    // ====== Book metadata operations ======

    /**
     * Saves book metadata under the "books/{bookId}" path.
     *
     * @param book a Book model containing id, title, description, genre, authorIds, и downloadLink
     */
    public void saveBookMetadata(Book book) {
        db.child(BOOKS).child(book.id).setValue(book);
    }

    /**
     * Uploads raw book bytes to Firebase Storage. Once a download URL is obtained,
     * the method saves the Book metadata in Realtime Database.
     *
     * @param bookId identifier of the book to update
     * @param key    the child key under "books/{bookId}" to update
     * @param value  new value to set at that key
     */
    public void updateBookField(String bookId, String key, Object value) {
        db.child(BOOKS).child(bookId).child(key).setValue(value);
    }
}
