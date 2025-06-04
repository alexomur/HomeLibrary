package com.example.homelibrary.ui.auth;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.homelibrary.R;
import com.example.homelibrary.data.AuthManager;

/**
 * Base fragment that handles AuthViewModel state and navigation
 * for both Login and Register screens. On success, notifies MainActivity.
 */
public abstract class AuthFragment extends Fragment {

    protected AuthViewModel viewModel;
    private ProgressBar progressBar;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        progressBar = view.findViewById(R.id.progress_bar);
        viewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        viewModel.getStatus().observe(getViewLifecycleOwner(), status -> {
            switch (status) {
                case LOADING:
                    setProgress(true);
                    break;
                case SUCCESS:
                    setProgress(false);
                    notifyHostSuccess();
                    break;
                case ERROR:
                    setProgress(false);
                    showError(viewModel.getErrorMessage().getValue());
                    break;
                default:
                    setProgress(false);
            }
        });
    }

    private void setProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Notify hosting Activity that login/register succeeded.
     */
    private void notifyHostSuccess() {
        if (getActivity() instanceof NavigationListener) {
            ((NavigationListener) getActivity()).onNeedShowHome();
        }
    }

    protected void showError(@Nullable String message) {
        if (message == null || message.isEmpty()) {
            message = getString(R.string.auth_error);
        }
        Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
    }

    /**
     * Interface to notify MainActivity to show HomeHostFragment.
     */
    public interface NavigationListener {
        void onNeedShowHome();
    }
}
