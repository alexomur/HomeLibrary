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
 * Handles user login flow. Extends AuthFragment for shared authentication logic.
 */
public class LoginFragment extends AuthFragment {

    private EditText etEmail;
    private EditText etPassword;
    private Button btnLogin;
    private TextView txtToRegister;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.et_email);
        etPassword = view.findViewById(R.id.et_password);
        btnLogin = view.findViewById(R.id.btn_login);
        txtToRegister = view.findViewById(R.id.tv_go_to_register);

        if (viewModel.isUserLoggedIn()) {
            requireActivity().finish();
            return;
        }

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();
            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)) {
                showError(getString(R.string.error_enter_email_password));
                return;
            }
            viewModel.login(email, pass);
        });

        txtToRegister.setOnClickListener(v ->
                Navigation.findNavController(view)
                        .navigate(R.id.action_loginFragment_to_registerFragment)
        );
    }
}
