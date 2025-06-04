package com.example.homelibrary.ui.book;

import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.homelibrary.R;
import com.example.homelibrary.data.AuthManager;
import com.example.homelibrary.data.BookDownloadManager;
import com.example.homelibrary.data.models.Author;
import com.example.homelibrary.data.models.Book;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Displays detailed information about a single book:
 * cover image, title, authors, genre, description and "Read" button.
 * When user taps "Read", download happens in background with a ProgressBar,
 * then opens any external PDF reader via Intent.
 */
public class BookDetailFragment extends Fragment {

    private static final String ARG_BOOK_ID = "bookId";

    private ImageView coverImage;
    private TextView titleText;
    private TextView authorsText;
    private TextView genreText;
    private TextView descriptionText;
    private Button readButton;
    private ProgressBar progressBar;

    private String bookId;
    private Book currentBook;
    private long downloadId = -1;

    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != downloadId) {
                return;
            }
            // Check if download succeeded
            boolean success = BookDownloadManager.getInstance(requireContext())
                    .isDownloadSuccessful(id);
            progressBar.setVisibility(View.GONE);
            readButton.setEnabled(true);
            if (success) {
                File localFile = new File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                        "books/" + currentBook.id + ".pdf"
                );
                openPdf(localFile);
            } else {
                Toast.makeText(requireContext(), "Download failed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    public BookDetailFragment() { }

    /**
     * Factory method to create a new instance with bookId argument.
     */
    public static BookDetailFragment newInstance(String bookId) {
        BookDetailFragment fragment = new BookDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_BOOK_ID, bookId);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_book_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        coverImage = view.findViewById(R.id.book_cover_image);
        titleText = view.findViewById(R.id.book_title);
        authorsText = view.findViewById(R.id.book_authors);
        genreText = view.findViewById(R.id.book_genre);
        descriptionText = view.findViewById(R.id.book_description);
        readButton = view.findViewById(R.id.button_read);
        progressBar = view.findViewById(R.id.book_progress_bar);

        if (getArguments() != null) {
            bookId = getArguments().getString(ARG_BOOK_ID);
            loadBookDetails();
        }

        readButton.setOnClickListener(v -> {
            if (currentBook != null) {
                File localFile = new File(
                        requireContext().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS),
                        "books/" + currentBook.id + ".pdf"
                );
                if (localFile.exists()) {
                    openPdf(localFile);
                } else {
                    startDownloadAndListen();
                }
            }
        });
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    public void onResume() {
        super.onResume();
        // Register receiver with proper flags
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requireContext().registerReceiver(
                    downloadReceiver,
                    filter,
                    Context.RECEIVER_NOT_EXPORTED
            );
        } else {
            requireContext().registerReceiver(downloadReceiver, filter);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the receiver
        try {
            requireContext().unregisterReceiver(downloadReceiver);
        } catch (IllegalArgumentException ignored) { }
    }

    /**
     * Fetches Book object from Realtime Database and populates UI.
     */
    private void loadBookDetails() {
        DatabaseReference bookRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("books")
                .child(bookId);
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Book book = snapshot.getValue(Book.class);
                if (book != null) {
                    currentBook = book;
                    populateBookData(book);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "Failed to load book", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Populates UI elements with Book data and fetches author names.
     */
    private void populateBookData(Book book) {
        titleText.setText(book.title);
        genreText.setText(book.genre);
        descriptionText.setText(book.description);

        Glide.with(requireContext())
                .load(book.imageUrl)
                .placeholder(R.drawable.placeholder_cover)
                .into(coverImage);

        loadAuthors(book.authorIds);
    }

    /**
     * Fetches each author name by ID and displays a comma-separated list.
     */
    private void loadAuthors(List<String> authorIds) {
        if (authorIds == null || authorIds.isEmpty()) {
            authorsText.setText(R.string.unknown_authors);
            return;
        }
        List<String> names = new ArrayList<>();
        DatabaseReference authorsRoot = FirebaseDatabase.getInstance()
                .getReference()
                .child("authors");
        for (String aId : authorIds) {
            authorsRoot.child(aId).addListenerForSingleValueEvent(
                    new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Author author = snapshot.getValue(Author.class);
                            if (author != null) {
                                names.add(author.fullName);
                            } else {
                                names.add(getString(R.string.unknown_author));
                            }
                            if (names.size() == authorIds.size()) {
                                authorsText.setText(String.join(", ", names));
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            names.add(getString(R.string.unknown_author));
                            if (names.size() == authorIds.size()) {
                                authorsText.setText(String.join(", ", names));
                            }
                        }
                    });
        }
    }

    /**
     * Starts download via BookDownloadManager and shows ProgressBar.
     */
    private void startDownloadAndListen() {
        String userId = AuthManager.getInstance().getCurrentUser().getUid();
        progressBar.setVisibility(View.VISIBLE);
        readButton.setEnabled(false);
        downloadId = BookDownloadManager.getInstance(requireContext())
                .downloadBook(currentBook, userId);
    }

    /**
     * Opens the local PDF file via external reader app.
     */
    private void openPdf(File file) {
        Uri uri = FileProvider.getUriForFile(
                requireContext(),
                requireContext().getPackageName() + ".fileprovider",
                file
        );
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        try {
            startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            Toast.makeText(requireContext(), "No PDF reader found", Toast.LENGTH_SHORT).show();
        }
    }
}
