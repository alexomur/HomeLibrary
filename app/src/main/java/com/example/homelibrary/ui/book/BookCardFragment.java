package com.example.homelibrary.ui.book;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.bumptech.glide.Glide;
import com.example.homelibrary.R;
import com.example.homelibrary.data.models.Author;
import com.example.homelibrary.data.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Small card with cover, title, authors, genre. Navigates to BookDetailFragment on click.
 */
public class BookCardFragment extends Fragment {

    private static final String ARG_BOOK_ID = "bookId";
    private String bookId;

    public static BookCardFragment newInstance(String bookId) {
        BookCardFragment f = new BookCardFragment();
        Bundle b = new Bundle();
        b.putString(ARG_BOOK_ID, bookId);
        f.setArguments(b);
        return f;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_card, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        if (getArguments() != null) bookId = getArguments().getString(ARG_BOOK_ID);

        ImageView cover = v.findViewById(R.id.card_cover);
        TextView title = v.findViewById(R.id.card_title);
        TextView authors = v.findViewById(R.id.card_authors);
        TextView genre = v.findViewById(R.id.card_genre);
        CardView root = v.findViewById(R.id.card_root);

        FirebaseDatabase.getInstance().getReference()
                .child("books").child(bookId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot ds) {
                        Book b = ds.getValue(Book.class);
                        if (b == null) return;
                        title.setText(b.title);
                        genre.setText(b.genre);
                        Glide.with(requireContext()).load(b.imageUrl)
                                .placeholder(R.drawable.placeholder_cover).into(cover);
                        fetchAuthors(b.authorIds, authors);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {}
                });

        root.setOnClickListener(v1 -> {
            NavController nav = NavHostFragment.findNavController(this);
            Bundle args = new Bundle();
            args.putString("bookId", bookId);
            nav.navigate(R.id.bookDetailFragment, args);
        });
    }

    private void fetchAuthors(List<String> ids, TextView out) {
        if (ids == null || ids.isEmpty()) { out.setText(R.string.unknown_authors); return; }
        List<String> list = new ArrayList<>();
        for (String id : ids) {
            FirebaseDatabase.getInstance().getReference()
                    .child("authors").child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(@NonNull DataSnapshot ds) {
                            Author a = ds.getValue(Author.class);
                            list.add(a != null ? a.fullName : getString(R.string.unknown_author));
                            if (list.size() == ids.size())
                                out.setText(String.join(", ", list));
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) {}
                    });
        }
    }
}
