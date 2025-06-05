package com.example.homelibrary.ui;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.homelibrary.R;
import com.example.homelibrary.data.AuthManager;
import com.example.homelibrary.ui.auth.AuthHostFragment;
import com.example.homelibrary.ui.auth.AuthFragment.NavigationListener;
import com.example.homelibrary.ui.home.HomeHostFragment;

/**
 * Single entry Activity. Shows AuthHostFragment or HomeHostFragment
 * based on user's authentication state.
 */
public class MainActivity extends AppCompatActivity implements NavigationListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (AuthManager.getInstance().getCurrentUser() != null) {
            showHome();
        } else {
            showAuth();
        }
    }

    private void showAuth() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, new AuthHostFragment());
        ft.commit();
    }

    private void showHome() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.main_fragment_container, new HomeHostFragment());
        ft.commit();
    }

    @Override
    public void onNeedShowHome() {
        showHome();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (AuthManager.getInstance().getCurrentUser() == null) {
            showAuth();
        }
    }
}
