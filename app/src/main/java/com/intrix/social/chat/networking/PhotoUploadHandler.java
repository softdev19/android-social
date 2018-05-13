package com.intrix.social.chat.networking;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.intrix.social.utils.Toaster;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yarolegovich on 7/29/15.
 */
public class PhotoUploadHandler {
    private static final String LOG_TAG = PhotoUploadHandler.class.getSimpleName();

    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    private static final int RESULT_LOAD_IMG = 1;

    private static Uri mFileUri;

    public static void handleFileUploadResult(Context context, int requestCode, int resultCode, Intent data) {

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode != Activity.RESULT_OK) {
                Toaster.showToast("Image capturing failed");
                mFileUri = null;
            }
        } else if (requestCode == RESULT_LOAD_IMG && resultCode == Activity.RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = context.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String imgPath = cursor.getString(columnIndex);
            cursor.close();
            try {
                File file = new File(imgPath);
                mFileUri = Uri.fromFile(file);
            } catch (Exception e) {
                Toaster.showToast("Unable to load this image");
                mFileUri = null;
            }
        } else mFileUri = null;

        if (mFileUri != null) {
            Intent startUploadIntent = new Intent(context, PhotoUploadService.class);
            startUploadIntent.putExtra(PhotoUploadService.FILE_EXTRA, mFileUri.getPath());
            context.startService(startUploadIntent);
        }
    }

    public static void loadFromGallery(Fragment fragment) {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        fragment.startActivityForResult(galleryIntent, RESULT_LOAD_IMG);
    }

    public static void takePhoto(Fragment fragment) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mFileUri = getOutputMediaFileUri();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mFileUri);
        fragment.startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    private static Uri getOutputMediaFileUri() {
        return Uri.fromFile(getOutputMediaFile());
    }

    private static File getOutputMediaFile() {
        File mediaStorageDir = new File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                NetworkConfigs.IMAGE_DIRECTORY_NAME);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.e(LOG_TAG, "Failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File mediaFile;
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    public static boolean deviceSupportsCamera(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }


}
