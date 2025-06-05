package com.example.homelibrary.ui.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.LocaleList;

import androidx.annotation.NonNull;
import androidx.preference.PreferenceManager;

import java.util.Locale;

/**
 * Handles runtime locale switching (ru / en).
 */
public final class LocaleUtil {

    private static final String KEY_LANG = "app_lang";

    private LocaleUtil() {}

    public static void applySaved(@NonNull Context ctx) {
        SharedPreferences p = PreferenceManager.getDefaultSharedPreferences(ctx);
        String code = p.getString(KEY_LANG, Locale.getDefault().getLanguage());
        updateLocale(ctx, code);
    }

    public static void setLang(@NonNull Context ctx, @NonNull String code) {
        PreferenceManager.getDefaultSharedPreferences(ctx)
                .edit().putString(KEY_LANG, code).apply();
        updateLocale(ctx, code);
    }

    private static void updateLocale(@NonNull Context ctx, @NonNull String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration cfg = ctx.getResources().getConfiguration();
        cfg.setLocales(new LocaleList(locale));
        ctx.getResources().updateConfiguration(cfg, ctx.getResources().getDisplayMetrics());
    }
}
