package com.example.homelibrary.ui.main;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.homelibrary.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        NavController navController =
                ((NavHostFragment) Objects.requireNonNull(getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_home)))
                        .getNavController();

        BottomNavigationView bottom =
                findViewById(R.id.bottom_navigation);
        bottom.setLabelVisibilityMode(
                BottomNavigationView.LABEL_VISIBILITY_LABELED);
        NavigationUI.setupWithNavController(bottom, navController);

        SearchView search = findViewById(R.id.search_view);
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String q) {
                var args = new Bundle();
                args.putString("query", q);
                navController.navigate(R.id.search_nav_graph, args);
                search.clearFocus();
                return true;
            }
            @Override public boolean onQueryTextChange(String s) { return false; }
        });
    }
}
