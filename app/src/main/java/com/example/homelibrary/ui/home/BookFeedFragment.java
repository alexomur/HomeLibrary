package com.example.homelibrary.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.homelibrary.R;
import com.example.homelibrary.data.models.Author;
import com.example.homelibrary.data.models.Book;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Scrollable real-time book feed. Pagination упрощена до первого экрана,
 * акцент на подписке на изменения конкретных книг.
 */
public class BookFeedFragment extends Fragment {

    public static final String ARG_QUERY = "query";

    private RecyclerView recycler;
    private final BookFeedAdapter adapter = new BookFeedAdapter();
    private String query;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_feed, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle s) {
        recycler = view.findViewById(R.id.recycler_book_feed);
        recycler.setLayoutManager(new GridLayoutManager(requireContext(), 1));
        recycler.setAdapter(adapter);

        if (getArguments() != null) {
            query = getArguments().getString(ARG_QUERY, "");
        }
        subscribeToBooks();
    }

    /** Live-подписка на узел "books". */
    private void subscribeToBooks() {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("books");

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot ds, @Nullable String prev) {
                Book b = ds.getValue(Book.class);
                if (b == null) return;
                if (matchesQuery(b)) {
                    adapter.addOrUpdate(b);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot ds, @Nullable String prev) {
                Book b = ds.getValue(Book.class);
                if (b == null) return;
                adapter.addOrUpdate(b);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot ds) {
                Book b = ds.getValue(Book.class);
                if (b == null) return;
                adapter.remove(b.id);
            }

            @Override public void onChildMoved(@NonNull DataSnapshot d, @Nullable String p) { }
            @Override public void onCancelled(@NonNull DatabaseError e) {
                Snackbar.make(requireView(), R.string.error_loading_books, Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private boolean matchesQuery(Book b) {
        return query.isEmpty() ||
                (b.title != null && b.title.toLowerCase().contains(query.toLowerCase()));
    }

    private class BookFeedAdapter extends RecyclerView.Adapter<BookFeedAdapter.Holder> {

        private final List<Book> data = new ArrayList<>();

        void addOrUpdate(Book book) {
            int idx = indexOf(book.id);
            if (idx == -1) {
                data.add(book);
                notifyItemInserted(data.size() - 1);
            } else {
                data.set(idx, book);
                notifyItemChanged(idx);
            }
        }

        void remove(String bookId) {
            int idx = indexOf(bookId);
            if (idx != -1) {
                data.remove(idx);
                notifyItemRemoved(idx);
            }
        }

        private int indexOf(String id) {
            for (int i = 0; i < data.size(); i++) {
                if (data.get(i).id.equals(id)) return i;
            }
            return -1;
        }

        @NonNull
        @Override
        public Holder onCreateViewHolder(@NonNull ViewGroup p, int vt) {
            View v = LayoutInflater.from(p.getContext())
                    .inflate(R.layout.item_book_card, p, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull Holder h, int pos) {
            h.bind(data.get(pos));
        }

        @Override
        public int getItemCount() {
            return data.size();
        }

        class Holder extends RecyclerView.ViewHolder {

            private final android.widget.ImageView cover = itemView.findViewById(R.id.card_cover);
            private final android.widget.TextView title = itemView.findViewById(R.id.card_title);
            private final android.widget.TextView authors = itemView.findViewById(R.id.card_authors);
            private final android.widget.TextView genre = itemView.findViewById(R.id.card_genre);

            private DatabaseReference bookRef;
            private com.google.firebase.database.ValueEventListener bookListener;

            Holder(View v) {
                super(v);
                v.setOnClickListener(v1 -> {
                    int pos = getAdapterPosition();
                    if (pos == RecyclerView.NO_POSITION) return;
                    Book b = data.get(pos);
                    NavController nav = NavHostFragment.findNavController(BookFeedFragment.this);
                    Bundle args = new Bundle();
                    args.putString("bookId", b.id);
                    nav.navigate(R.id.bookDetailFragment, args);
                });
            }

            void bind(Book b) {
                title.setText(b.title);
                genre.setText(b.genre);
                Glide.with(requireContext())
                        .load(b.imageUrl)
                        .placeholder(R.drawable.placeholder_cover)
                        .into(cover);
                subscribeAuthors(b.authorIds);

                // обновление при изменении книги
                if (bookRef != null && bookListener != null) {
                    bookRef.removeEventListener(bookListener);
                }
                bookRef = FirebaseDatabase.getInstance()
                        .getReference("books").child(b.id);
                bookListener = (com.google.firebase.database.ValueEventListener) (new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot ds) {
                        Book fresh = ds.getValue(Book.class);
                        if (fresh == null) return;
                        title.setText(fresh.title);
                        genre.setText(fresh.genre);
                        Glide.with(requireContext())
                                .load(fresh.imageUrl)
                                .placeholder(R.drawable.placeholder_cover)
                                .into(cover);
                    }
                    @Override public void onCancelled(@NonNull DatabaseError e) { }
                });
                bookRef.addValueEventListener(bookListener);
            }

            private void subscribeAuthors(List<String> ids) {
                if (ids == null || ids.isEmpty()) {
                    authors.setText(R.string.unknown_authors);
                    return;
                }
                Map<String, String> cache = new HashMap<>();
                for (String id : ids) {
                    FirebaseDatabase.getInstance().getReference("authors")
                            .child(id)
                            .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot ds) {
                                    Author a = ds.getValue(Author.class);
                                    cache.put(id, a != null ? a.fullName : getString(R.string.unknown_author));
                                    if (cache.size() == ids.size()) {
                                        List<String> list = new ArrayList<>();
                                        for (String k : ids) list.add(cache.get(k));
                                        authors.setText(String.join(", ", list));
                                    }
                                }
                                @Override public void onCancelled(@NonNull DatabaseError e) { }
                            });
                }
            }
        }
    }
}
