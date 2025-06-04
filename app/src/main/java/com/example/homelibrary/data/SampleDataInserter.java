package com.example.homelibrary.data;

import com.example.homelibrary.data.models.Author;
import com.example.homelibrary.data.models.Book;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;

public class SampleDataInserter {

    public static void insertPushkinAndOnegin() {
        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();

        DatabaseReference authorsRef = rootRef.child("authors");
        String authorId = authorsRef.push().getKey();

        Author pushkin = new Author();
        pushkin.id = authorId;
        pushkin.fullName = "А.С. Пушкин";
        pushkin.biography = "Александр Сергеевич Пушкин — русский поэт, драматург и прозаик.";
        pushkin.bookIds = Collections.emptyList();

        DBManager.getInstance().saveAuthor(pushkin);

        DatabaseReference booksRef = rootRef.child("books");
        String bookId = booksRef.push().getKey();

        Book onegin = new Book();
        onegin.id = bookId;
        onegin.title = "Евгений Онегин";
        onegin.description = "Роман в стихах, самый известный труд Пушкина.";
        onegin.genre = "Поэма";
        onegin.authorIds = Collections.singletonList(authorId);
        onegin.downloadUrl = "https://imwerden.de/pdf/pushkin_evgenij_onegin.pdf";
        onegin.imageUrl = "https://upload.wikimedia.org/wikipedia/commons/e/ed/Eugene_Onegin_book_edition.jpg";

        DBManager.getInstance().saveBookMetadata(onegin);

        DBManager.getInstance().updateAuthorField(authorId, "bookIds", Collections.singletonList(bookId));
    }
}
