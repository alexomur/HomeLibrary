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

import com.example.homelibrary.data.models.Book;

/**
 * Manages downloading of book files from given URLs and saving them locally.
 * Uses Android's DownloadManager system service.
 */
public class BookDownloadManager {

    private static BookDownloadManager instance;

    private final Context context;
    private final DownloadManager systemDownloadManager;
    private long lastDownloadId = -1;

    private BookDownloadManager(Context ctx) {
        context = ctx.getApplicationContext();
        systemDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
    }

    public static synchronized BookDownloadManager getInstance(Context ctx) {
        if (instance == null) {
            instance = new BookDownloadManager(ctx);
        }
        return instance;
    }

    /**
     * Starts downloading a book file from a URL and returns the downloadId.
     *
     * @param book   Book model containing downloadUrl and id.
     * @param userId ID of the current user.
     * @return system download ID to track completion.
     */
    public long downloadBook(Book book, String userId) {
        Uri downloadUri = Uri.parse(book.downloadUrl);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);

        request.setTitle(book.title);
        request.setDescription("Downloading " + book.title);
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);

        java.io.File dir = new java.io.File(
                context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "books");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        java.io.File outputFile = new java.io.File(dir, book.id + ".pdf");
        request.setDestinationUri(Uri.fromFile(outputFile));

        long downloadId = systemDownloadManager.enqueue(request);
        lastDownloadId = downloadId;
        return downloadId;
    }

    /**
     * Checks if the given downloadId has completed successfully.
     * @param downloadId ID returned by enqueue(...)
     * @return true if status == STATUS_SUCCESSFUL, false otherwise (or still pending).
     */
    public boolean isDownloadSuccessful(long downloadId) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(downloadId);
        Cursor cursor = systemDownloadManager.query(query);
        if (cursor != null) {
            boolean success = false;
            if (cursor.moveToFirst()) {
                int statusIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                int status = cursor.getInt(statusIndex);
                success = (status == DownloadManager.STATUS_SUCCESSFUL);
            }
            cursor.close();
            return success;
        }
        return false;
    }

    /**
     * Returns the last enqueued downloadId (for convenience).
     */
    public long getLastDownloadId() {
        return lastDownloadId;
    }
}
