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

/** Standard email-password sign-in screen. */
public class LoginFragment extends AuthFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText email = view.findViewById(R.id.et_email);
        EditText password = view.findViewById(R.id.et_password);
        Button signIn = view.findViewById(R.id.btn_login);
        TextView goToReg = view.findViewById(R.id.tv_go_to_register);

        signIn.setOnClickListener(v -> {
            var e = email.getText().toString().trim();
            var p = password.getText().toString().trim();
            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p)) {
                showError(getString(R.string.error_enter_email_password));
                return;
            }
            viewModel.login(e, p);
        });

        goToReg.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_login_to_register)
        );
    }
}
