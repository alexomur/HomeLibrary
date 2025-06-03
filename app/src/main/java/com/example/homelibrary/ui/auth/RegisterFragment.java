package com.example.homelibrary.ui.auth;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.example.homelibrary.R;

/**
 * Handles user registration flow. Extends AuthFragment for shared authentication logic.
 */
public class RegisterFragment extends AuthFragment {

    private EditText etEmail;
    private EditText etPassword;
    private EditText etConfirmPassword;
    private Button btnRegister;
    private TextView txtToLogin;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        etConfirmPassword = view.findViewById(R.id.et_confirm_password);
        btnRegister = view.findViewById(R.id.btn_register);
        txtToLogin = view.findViewById(R.id.tv_go_to_login);

        btnRegister.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String confirmPass = etConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email) ||
                    TextUtils.isEmpty(password) ||
                    TextUtils.isEmpty(confirmPass)) {
                showError(getString(R.string.error_fill_all_fields));
                return;
            }
            if (!password.equals(confirmPass)) {
                showError(getString(R.string.error_passwords_do_not_match));
                return;
            }
            viewModel.register(email, password);
        });

        txtToLogin.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_registerFragment_to_loginFragment)
        );
    }
}
