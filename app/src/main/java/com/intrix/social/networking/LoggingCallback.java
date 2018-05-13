package com.intrix.social.networking;

import android.util.Log;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class LoggingCallback<T> implements Callback<T> {

    private static final String LOG_TAG = LoggingCallback.class.getSimpleName();

    private com.intrix.social.utils.Callback<T> onSuccess;

    public LoggingCallback() { }

    public LoggingCallback(com.intrix.social.utils.Callback<T> onSuccess) {
        this.onSuccess = onSuccess;
    }

    @Override
    public void success(T t, Response response) {
        Log.d(LOG_TAG, "Received: " + response.getStatus());
        Log.d(LOG_TAG, String.valueOf(t));
        if (onSuccess != null) {
            onSuccess.onResult(t);
        }
    }

    @Override
    public void failure(RetrofitError error) {
        Log.e(LOG_TAG, "Error occurred: " + error.getMessage());
    }
}
