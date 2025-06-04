package com.example.homelibrary.data;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import androidx.core.content.ContextCompat;

import com.example.homelibrary.data.models.Book;

/**
 * Manages downloading of book files from given URLs and saving them locally.
 * Uses Android's DownloadManager system service.
 */
public class BookDownloadManager {

    private static BookDownloadManager instance;
    private final Context context;
    private final DownloadManager systemDownloadManager;
    private final DBManager dbManager;
    private long lastDownloadId = -1;

    private BookDownloadManager(Context ctx) {
        context = ctx.getApplicationContext();
        systemDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        dbManager = DBManager.getInstance();
        registerDownloadReceiver();
    }

    public static synchronized BookDownloadManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new BookDownloadManager(ctx);
        }
        return instance;
    }

    /**
     * Starts downloading a book file from a URL and records the download in user's profile.
     *
     * @param book   Book model containing downloadLink and id.
     * @param userId ID of the current user.
     */
    public void downloadBook(Book book, String userId) {
        Uri downloadUri = Uri.parse(book.downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setTitle(book.title);
        request.setDescription("Downloading " + book.title);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);

        java.io.File dir = new java.io.File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "books");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        java.io.File outputFile = new java.io.File(dir, book.id + ".pdf");
        request.setDestinationUri(Uri.fromFile(outputFile));

        lastDownloadId = systemDownloadManager.enqueue(request);
        dbManager.updateUserField(userId, "downloadedBooks/" + book.id, 0);
    }

    /**
     * Registers BroadcastReceiver to listen for completed downloads.
     */
    private void registerDownloadReceiver() {
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
        } else {
            ContextCompat.registerReceiver(context, downloadReceiver, filter, ContextCompat.RECEIVER_NOT_EXPORTED);
        }
    }

    /**
     * Unregisters the download receiver.
     */
    public void unregisterDownloadReceiver() {
        try {
            context.unregisterReceiver(downloadReceiver);
        } catch (IllegalArgumentException ignored) {
        }
    }

    /**
     * BroadcastReceiver to handle download completion.
     */
    private final BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context ctx, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (id != lastDownloadId) {
                return;
            }

            DownloadManager.Query query = new DownloadManager.Query();
            query.setFilterById(id);
            Cursor cursor = systemDownloadManager.query(query);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(statusIndex);
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        // File downloaded successfully
                    } else {
                        // Download failed; optionally remove DB entry or retry
                        int uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_URI);
                        String uriString = cursor.getString(uriIndex);
                        // Parse bookId from filename if needed
                    }
                }
                cursor.close();
            }
        }
    };
}
