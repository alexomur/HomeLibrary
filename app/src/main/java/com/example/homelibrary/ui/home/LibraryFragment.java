package com.example.homelibrary.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.homelibrary.R;

public class LibraryFragment extends Fragment {

    private SearchView searchView;
    private ImageButton backButton;
    private String currentQuery = null;

    /**
     * Inflates the layout for this fragment.
     *
     * @param inflater           LayoutInflater used to inflate views
     * @param container          Parent view that this fragment's UI will attach to
     * @param savedInstanceState Previously saved state, if any
     * @return Root view of the fragment layout
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    /**
     * Initializes views, search logic, and back navigation handling.
     *
     * @param view               Root view returned by onCreateView
     * @param savedInstanceState Previously saved state, if any
     */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchView = view.findViewById(R.id.library_search_view);
        backButton = view.findViewById(R.id.btn_back);

        initSearch();
        initBackButton();
        initSystemBackHandler();

        if (savedInstanceState == null) {
            openFeed(null);
        }
    }

    /**
     * Configures the SearchView listener to perform searches and show the back button.
     */
    private void initSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String trimmed = query.trim();
                if (!trimmed.isEmpty()) {
                    currentQuery = trimmed;
                    showBackButton(true);
                    openFeed(trimmed);
                }
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    /**
     * Configures the back button to reset the search and reload the full book feed.
     */
    private void initBackButton() {
        backButton.setOnClickListener(v -> {
            currentQuery = null;
            showBackButton(false);
            searchView.setQuery("", false);
            searchView.clearFocus();
            openFeed(null);
        });
    }

    /**
     * Sets up handling of the device back button to mirror the UI back button when a search is active.
     */
    private void initSystemBackHandler() {
        requireActivity().getOnBackPressedDispatcher()
                .addCallback(getViewLifecycleOwner(), new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (currentQuery != null) {
                            currentQuery = null;
                            showBackButton(false);
                            searchView.setQuery("", false);
                            openFeed(null);
                        } else {
                            setEnabled(false);
                            requireActivity().onBackPressed();
                        }
                    }
                });
    }

    /**
     * Toggles visibility of the back button. Uses INVISIBLE to reserve space.
     *
     * @param show True to show, false to hide
     */
    private void showBackButton(boolean show) {
        backButton.setVisibility(show ? View.VISIBLE : View.INVISIBLE);
    }

    /**
     * Replaces the child fragment with BookFeedFragment, passing an optional query argument.
     *
     * @param query Text query to filter book feed, or null for full feed
     */
    private void openFeed(@Nullable String query) {
        Bundle args = new Bundle();
        if (query != null && !query.isEmpty()) {
            args.putString(BookFeedFragment.ARG_QUERY, query);
        }
        FragmentTransaction ft = getChildFragmentManager().beginTransaction();
        ft.replace(R.id.library_feed_container, BookFeedFragment.class, args);
        ft.commit();
    }
}
