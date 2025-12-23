package com.pasta.twrp;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileSaveHelper {

    public interface SaveCallback {
        void onSaveSuccess(String filePath, long fileSize);
        void onSaveError(String error);
    }

    public static void saveDeviceInfo(Context context, String deviceInfo, String codename, SaveCallback callback) {
        try {
            String fileName = "twrp-builder-" + codename + ".txt";
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+ - use MediaStore API
                saveWithMediaStore(context, deviceInfo, fileName, callback);
            } else {
                // Android 9 and below - use legacy file system
                saveLegacy(context, deviceInfo, fileName, callback);
            }
        } catch (Exception e) {
            callback.onSaveError(e.getMessage());
        }
    }

    private static void saveWithMediaStore(Context context, String deviceInfo, String fileName, SaveCallback callback) {
        try {
            ContentResolver resolver = context.getContentResolver();
            ContentValues contentValues = new ContentValues();
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName);
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "text/plain");
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues);
            if (uri == null) {
                callback.onSaveError("Failed to create file in MediaStore");
                return;
            }

            try (OutputStream outputStream = resolver.openOutputStream(uri)) {
                if (outputStream == null) {
                    callback.onSaveError("Failed to open output stream");
                    return;
                }
                outputStream.write(deviceInfo.getBytes());
                outputStream.flush();
            }

            // Verify the file was saved
            long fileSize = deviceInfo.getBytes().length;
            String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS) 
                + File.separator + fileName;
            
            callback.onSaveSuccess(filePath, fileSize);
        } catch (Exception e) {
            callback.onSaveError(e.getMessage());
        }
    }

    private static void saveLegacy(Context context, String deviceInfo, String fileName, SaveCallback callback) {
        try {
            File downloadsDir = new File(Environment.getExternalStorageDirectory(), "Download");
            
            if (!downloadsDir.exists()) {
                downloadsDir.mkdirs();
            }
            
            File file = new File(downloadsDir, fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(deviceInfo.getBytes());
            fos.close();
            
            // Verify the file exists and get size
            if (file.exists()) {
                callback.onSaveSuccess(file.getAbsolutePath(), file.length());
            } else {
                callback.onSaveError("File was not created");
            }
        } catch (Exception e) {
            callback.onSaveError(e.getMessage());
        }
    }
}
