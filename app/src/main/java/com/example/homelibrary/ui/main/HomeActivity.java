package com.example.homelibrary.ui.main;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.homelibrary.R;
import com.example.homelibrary.data.AuthManager;
import com.example.homelibrary.ui.auth.AuthActivity;

public class HomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Если пользователь не залогинен — отправляем его в AuthActivity
        if (AuthManager.getInstance().getCurrentUser() == null) {
            startActivity(new Intent(this, AuthActivity.class));
            finish(); // чтобы HomeActivity не остался позади
            return;
        }

        // Если залогинен — грузим основной макет HomeActivity
        setContentView(R.layout.activity_home);
        // Здесь вы подключаете BottomNavigation, NavHostFragment для главного графа приложения и т.п.
    }
}
