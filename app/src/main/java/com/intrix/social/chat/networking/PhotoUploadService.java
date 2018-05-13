package com.intrix.social.chat.networking;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by yarolegovich on 8/6/15.
 */
public class PhotoUploadService extends IntentService {

    private static final String LOG_TAG = PhotoUploadService.class.getSimpleName();

    public static final String ACTION_UPLOAD_FINISHED = "com.intrixtech.solve.PHOTO_UPLOADED";
    public static final String ACTION_UPLOAD_STARTED = "com.intrixtech.solve.UPLOAD_STARTED";

    public static final String FILE_EXTRA = "file_intent_extra";
    public static final String STATUS_EXTRA = "file_loaded_status";

    public PhotoUploadService() {
        super(PhotoUploadService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Intent resultIntent = new Intent(ACTION_UPLOAD_FINISHED);
        try {
            String fileName = intent.getStringExtra(FILE_EXTRA);
            Networker networker = Networker.getInstance();

            Intent uploadStartedIntent = new Intent(ACTION_UPLOAD_STARTED);
            uploadStartedIntent.putExtra(FILE_EXTRA, fileName);
            sendBroadcast(uploadStartedIntent);

            String response = networker.uploadFile(fileName);
            JSONObject jsonObject = new JSONObject(response);
            resultIntent.putExtra(STATUS_EXTRA, jsonObject.getBoolean("ok"));
        } catch (JSONException e) {
            resultIntent.putExtra(STATUS_EXTRA, false);
            Log.e(LOG_TAG, "Error in photo loading service " + e.getMessage(), e);
        }
        sendBroadcast(resultIntent);
    }
}
