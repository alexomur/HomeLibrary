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

/** Email-password sign-up screen. */
public class RegisterFragment extends AuthFragment {

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_register, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        EditText email = view.findViewById(R.id.et_email);
        EditText pass = view.findViewById(R.id.et_password);
        EditText confirm = view.findViewById(R.id.et_confirm_password);
        Button signUp = view.findViewById(R.id.btn_register);
        TextView goToLog = view.findViewById(R.id.tv_go_to_login);

        signUp.setOnClickListener(v -> {
            var e = email.getText().toString().trim();
            var p1 = pass.getText().toString().trim();
            var p2 = confirm.getText().toString().trim();

            if (TextUtils.isEmpty(e) || TextUtils.isEmpty(p1) || TextUtils.isEmpty(p2)) {
                showError(getString(R.string.error_fill_all_fields));
                return;
            }
            if (!p1.equals(p2)) {
                showError(getString(R.string.error_passwords_do_not_match));
                return;
            }
            viewModel.register(e, p1);
        });

        goToLog.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_register_to_login)
        );
    }
}
