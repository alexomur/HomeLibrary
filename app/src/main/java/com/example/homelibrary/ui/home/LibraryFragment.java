package com.example.homelibrary.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.homelibrary.R;
import com.example.homelibrary.ui.feed.BookFeedFragment;

/**
 * Library container with Toolbar search and embedded BookFeedFragment.
 */
public class LibraryFragment extends Fragment {

    private SearchView searchView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_library, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        searchView = view.findViewById(R.id.library_search_view);
        initSearch();
        if (savedInstanceState == null) {
            openFeed(null);
        }
    }

    private void initSearch() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                openFeed(query.trim());
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

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
