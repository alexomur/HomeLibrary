package com.example.homelibrary.ui.auth;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.homelibrary.R;
import com.example.homelibrary.data.AuthManager;
import com.example.homelibrary.ui.main.HomeActivity;

/**
 * Entry-point activity.
 * Shows login/registration screens when the user is not authenticated
 * and skips straight to {@link HomeActivity} otherwise.
 */
public class AuthActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (AuthManager.getInstance().getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_auth);
    }
}
