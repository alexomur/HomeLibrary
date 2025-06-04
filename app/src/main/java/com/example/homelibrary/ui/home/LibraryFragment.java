package com.example.homelibrary.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homelibrary.R;
import com.example.homelibrary.data.models.Author;
import com.example.homelibrary.data.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Shows library of books as a grid of cards.
 */
public class LibraryFragment extends Fragment {

    private RecyclerView recycler;
    private final List<Book> books = new ArrayList<>();
    private final BookAdapter adapter = new BookAdapter();

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle s) {
        recycler = v.findViewById(R.id.recycler_library);
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        recycler.setAdapter(adapter);
        loadBooks();
    }

    private void loadBooks() {
        FirebaseDatabase.getInstance().getReference()
                .child("books")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override public void onDataChange(@NonNull DataSnapshot ds) {
                        books.clear();
                        for (DataSnapshot snap : ds.getChildren()) {
                            Book b = snap.getValue(Book.class);
                            if (b != null) books.add(b);
                        }
                        adapter.notifyDataSetChanged();
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) {
                        Toast.makeText(requireContext(), "Failed to load books", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class BookAdapter extends RecyclerView.Adapter<BookAdapter.Holder> {

        @NonNull
        @Override public Holder onCreateViewHolder(@NonNull ViewGroup p, int vType) {
            View view = LayoutInflater.from(p.getContext())
                    .inflate(R.layout.item_book_card, p, false);
            return new Holder(view);
        }

        @Override public void onBindViewHolder(@NonNull Holder h, int pos) {
            h.bind(books.get(pos));
        }

        @Override public int getItemCount() { return books.size(); }

        class Holder extends RecyclerView.ViewHolder {

            private final ImageView cover;
            private final TextView title;
            private final TextView authors;
            private final TextView genre;
            private final CardView root;

            Holder(View v) {
                super(v);
                cover   = v.findViewById(R.id.card_cover);
                title   = v.findViewById(R.id.card_title);
                authors = v.findViewById(R.id.card_authors);
                genre   = v.findViewById(R.id.card_genre);
                root    = (CardView) v;
            }

            void bind(Book b) {
                title.setText(b.title);
                genre.setText(b.genre);
                Glide.with(requireContext())
                        .load(b.imageUrl)
                        .placeholder(R.drawable.placeholder_cover)
                        .into(cover);

                fetchAuthors(b.authorIds, authors);

                root.setOnClickListener(v1 -> {
                    NavController nav = NavHostFragment.findNavController(LibraryFragment.this);
                    Bundle args = new Bundle();
                    args.putString("bookId", b.id);
                    nav.navigate(R.id.action_library_to_bookDetail, args);
                });
            }
        }
    }

    private void fetchAuthors(List<String> ids, TextView out) {
        if (ids == null || ids.isEmpty()) {
            out.setText(R.string.unknown_authors);
            return;
        }
        Map<String, String> cache = new HashMap<>();
        for (String id : ids) {
            FirebaseDatabase.getInstance().getReference()
                    .child("authors").child(id)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override public void onDataChange(@NonNull DataSnapshot ds) {
                            Author a = ds.getValue(Author.class);
                            cache.put(id, a != null ? a.fullName : getString(R.string.unknown_author));
                            if (cache.size() == ids.size()) {
                                List<String> list = new ArrayList<>();
                                for (String k : ids) list.add(cache.get(k));
                                out.setText(String.join(", ", list));
                            }
                        }
                        @Override public void onCancelled(@NonNull DatabaseError e) {
                            cache.put(id, getString(R.string.unknown_author));
                            if (cache.size() == ids.size()) {
                                List<String> list = new ArrayList<>();
                                for (String k : ids) list.add(cache.get(k));
                                out.setText(String.join(", ", list));
                            }
                        }
                    });
        }
    }
}
