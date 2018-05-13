package com.intrix.social.networking;

import android.content.Intent;
import android.support.v4.util.Pair;
import android.util.Log;

import com.intrix.social.MainApplication;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Created by yarolegovich on 8/17/15.
 */
public class NetworkUtils {

    public static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    public static String readIs(InputStream is) {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        StringBuilder total = new StringBuilder();
        String line = "";
        try {
            while ((line = r.readLine()) != null) {
                total.append(line).append("\n");
            }
            line = total.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return line;
    }

    public static void sendApiActionBroadcast(String action, boolean created) {
        Intent in = new Intent();
        in.setAction(action);
        in.putExtra("status", created);
        MainApplication.instance.sendBroadcast(in);
    }

    public static int sendRawPostRequest(String url, List<Pair<String, String>> body) {
        try {
            JSONObject payload = new JSONObject();
            for (Pair<String, String> pair : body) {
                payload.put(pair.first, pair.second);
            }
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            OutputStream os = connection.getOutputStream();
            byte[] rawPayload = payload.toString().getBytes("UTF-8");
            os.write(rawPayload, 0, rawPayload.length);
            os.close();
            return connection.getResponseCode();
        } catch (Exception e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }
        return -1;
    }
}
