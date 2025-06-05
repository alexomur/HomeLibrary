package com.example.homelibrary.ui.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.TypedValue;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.homelibrary.R;

import java.util.Random;

/**
 * Generates letter avatars when user has no image.
 */
public final class AvatarUtil {

    private static final int[] COLORS = {
            0xfff44336, 0xffe91e63, 0xff9c27b0, 0xff673ab7, 0xff3f51b5,
            0xff2196f3, 0xff03a9f4, 0xff00bcd4, 0xff009688, 0xff4caf50,
            0xff8bc34a, 0xffff9800, 0xffff5722, 0xff795548, 0xff607d8b
    };

    private AvatarUtil() {}

    public static Bitmap create(@NonNull Context ctx, @NonNull String letter, int sizeDp) {
        int sizePx = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, sizeDp, ctx.getResources().getDisplayMetrics());

        Bitmap bm = Bitmap.createBitmap(sizePx, sizePx, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);

        Paint bg = new Paint(Paint.ANTI_ALIAS_FLAG);
        bg.setColor(randomColor(letter));
        c.drawCircle(sizePx / 2f, sizePx / 2f, sizePx / 2f, bg);

        Paint txt = new Paint(Paint.ANTI_ALIAS_FLAG);
        txt.setColor(ContextCompat.getColor(ctx, android.R.color.white));
        txt.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        txt.setTextSize(sizePx * 0.5f);
        txt.setTextAlign(Paint.Align.CENTER);

        Paint.FontMetrics fm = txt.getFontMetrics();
        float y = sizePx / 2f - (fm.ascent + fm.descent) / 2f;
        c.drawText(letter.toUpperCase(), sizePx / 2f, y, txt);

        return bm;
    }

    private static int randomColor(String seed) {
        int hash = seed.hashCode();
        int index = Math.abs(hash) % COLORS.length;
        return COLORS[index];
    }
}
