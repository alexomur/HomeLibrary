package com.example.homelibrary.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;
import androidx.appcompat.widget.SearchView;

import com.example.homelibrary.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

/**
 * Hosts Toolbar, NavHostFragment, and BottomNavigationView for main content.
 */
public class HomeHostFragment extends Fragment {

    private NavController navController;

    public HomeHostFragment() { }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        NavHostFragment navHostFragment = (NavHostFragment)
                Objects.requireNonNull(
                        getChildFragmentManager().findFragmentById(R.id.nav_host_home)
                );
        navController = navHostFragment.getNavController();

        BottomNavigationView bottom = view.findViewById(R.id.bottom_navigation);
        bottom.setLabelVisibilityMode(BottomNavigationView.LABEL_VISIBILITY_LABELED);
        NavigationUI.setupWithNavController(bottom, navController);

        SearchView search = view.findViewById(R.id.search_view);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Bundle args = new Bundle();
                args.putString("query", query);
                navController.navigate(R.id.search_nav_graph, args);
                search.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }
}
