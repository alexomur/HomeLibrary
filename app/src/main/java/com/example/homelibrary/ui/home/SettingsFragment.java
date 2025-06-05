package com.example.homelibrary.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.example.homelibrary.R;
import com.example.homelibrary.data.AuthManager;
import com.example.homelibrary.ui.MainActivity;
import com.example.homelibrary.ui.common.LocaleUtil;
import com.google.android.material.snackbar.Snackbar;

/**
 * Settings screen: nickname, email, avatar, language, logout.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    private static final String KEY_NICK   = "pref_nick";
    private static final String KEY_EMAIL  = "pref_email";
    private static final String KEY_AVATAR = "pref_avatar";
    private static final String KEY_LANG   = "pref_lang";
    private static final String KEY_LOGOUT = "pref_logout";

    /** Launcher для выбора изображения. */
    private final ActivityResultLauncher<Intent> avatarPicker =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    res -> {
                        if (res.getResultCode() == Activity.RESULT_OK && res.getData() != null) {
                            Uri uri = res.getData().getData();
                            if (uri != null) AuthManager.getInstance().updateAvatar(uri.toString());
                        }
                    });

    @Override
    public void onCreatePreferences(Bundle b, String rootKey) {
        setPreferencesFromResource(R.xml.prefs_settings, rootKey);
        initListeners();
    }

    private void initListeners() {
        EditTextPreference nick  = findPreference(KEY_NICK);
        EditTextPreference email = findPreference(KEY_EMAIL);
        Preference avatar        = findPreference(KEY_AVATAR);
        Preference lang          = findPreference(KEY_LANG);
        Preference logout        = findPreference(KEY_LOGOUT);

        if (nick != null) {
            nick.setOnPreferenceChangeListener((p, v) -> {
                AuthManager.getInstance().updateNickname(v.toString().trim());
                return true;
            });
        }

        if (email != null) {
            email.setOnPreferenceChangeListener((p, v) -> {
                AuthManager.getInstance().updateEmail(v.toString().trim())
                        .addOnFailureListener(e ->
                                Snackbar.make(requireView(), e.getMessage(),
                                        Snackbar.LENGTH_LONG).show());
                return true;
            });
        }

        if (avatar != null) {
            avatar.setOnPreferenceClickListener(p -> {
                Intent pick = new Intent(Intent.ACTION_GET_CONTENT).setType("image/*");
                avatarPicker.launch(Intent.createChooser(
                        pick, getString(R.string.choose_avatar)));
                return true;
            });
        }

        if (lang != null) {
            lang.setOnPreferenceClickListener(p -> {
                String cur = requireContext().getResources()
                        .getConfiguration().getLocales().get(0).getLanguage();
                LocaleUtil.setLang(requireContext(), cur.equals("ru") ? "en" : "ru");
                requireActivity().recreate();
                return true;
            });
        }

        if (logout != null) {
            logout.setOnPreferenceClickListener(p -> {
                AuthManager.getInstance().logout();
                requireActivity().recreate();
                return true;
            });
        }

    }
}
