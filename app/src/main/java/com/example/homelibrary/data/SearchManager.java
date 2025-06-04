package com.example.homelibrary.data;

import androidx.annotation.NonNull;

import com.example.homelibrary.data.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Provides methods to search books by title.
 * Since RTDB не поддерживает полнотекстовый поиск,
 * мы забираем все книги и фильтруем на клиенте.
 */
public class SearchManager {

    public interface SearchCallback {
        void onResults(List<Book> results);
        void onError(Exception e);
    }

    /**
     * Загружает все книги из RTDB и затем отбирает те,
     * у которых title содержит query (без учёта регистра).
     *
     * @param query    строка для поиска
     * @param callback колбэк, в который возвращается список найденных книг
     */
    public static void searchBooksByTitle(String query, SearchCallback callback) {
        FirebaseDatabase.getInstance()
                .getReference()
                .child("books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<Book> matched = new ArrayList<>();
                        String lowerQuery = query.toLowerCase();
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            Book b = snap.getValue(Book.class);
                            if (b != null && b.title != null) {
                                if (b.title.toLowerCase().contains(lowerQuery)) {
                                    matched.add(b);
                                }
                            }
                        }
                        callback.onResults(matched);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }
}
