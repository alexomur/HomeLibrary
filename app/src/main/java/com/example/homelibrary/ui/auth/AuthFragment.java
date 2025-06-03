package com.example.homelibrary.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.homelibrary.R;

/**
 * Abstract fragment containing shared logic for Login and Register
 * <p>
 *  Subclasses should:
 * <p>  - In onViewCreated(), find their own Views (EditText, Button, etc.).
 * <p>  - Attach viewModel.login(...) or viewModel.register(...) to their button clicks.
 * <p>  - (Optionally) override onAuthSuccess() if different post-auth logic is needed.
 */
public abstract class AuthFragment extends Fragment {

    protected AuthViewModel viewModel;
    private ProgressBar progressBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // 1) Find the common ProgressBar in the fragment layout
        progressBar = view.findViewById(R.id.progress_bar);

        // 2) Initialize the ViewModel
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        // 3) Observe LiveData<Status> from the ViewModel
        viewModel.getStatus().observe(getViewLifecycleOwner(), new Observer<AuthViewModel.Status>() {
            @Override
            public void onChanged(AuthViewModel.Status status) {
                switch (status) {
                    case LOADING:
                        if (progressBar != null) {
                            progressBar.setVisibility(View.VISIBLE);
                        }
                        break;
                    case SUCCESS:
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        onAuthSuccess();
                        break;
                    case ERROR:
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        String err = viewModel.getErrorMessage().getValue();
                        if (err == null || err.isEmpty()) {
                            err = getString(R.string.auth_error);
                        }
                        showError(err);
                        break;
                    case IDLE:
                    default:
                        if (progressBar != null) {
                            progressBar.setVisibility(View.GONE);
                        }
                        break;
                }
            }
        });
    }

    /**
     * Called when authentication (login/register) succeeds.
     * By default, finishes AuthActivity, returning to HomeActivity.
     * Subclasses may override for custom behavior.
     */
    protected void onAuthSuccess() {
        if (getActivity() != null) {
            getActivity().finish();
        }
    }

    /**
     * Displays an error message via Toast.
     * Subclasses may override to show errors differently.
     *
     * @param message the error text to display
     */
    protected void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(getContext(), message, Toast.LENGTH_LONG).show();
        }
    }
}
