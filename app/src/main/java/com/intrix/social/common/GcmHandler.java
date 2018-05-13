package com.intrix.social.common;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.Settings;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.intrix.social.Data;
import com.intrix.social.MainApplication;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.VolleyHelper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by sutharsha on 09/04/15.
 */
public class GcmHandler {

    String TAG = "Data";
    public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "gcm_reg_id";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    String SENDER_ID = "305947075904"; // hardcoded value - needs to be changed on launch

    private Context context;
    private Data data;

    GoogleCloudMessaging gcm;
    String regid;

    public GcmHandler(Context cont)
    {
        context = cont;
        MainApplication mainApp = (MainApplication)context;
        data = mainApp.data;
    }

    public void register()
    {
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(context);
            regid = getRegistrationId(context);

            //if (regid.isEmpty()) {
                registerInBackground();
            //}
        } else {
            Log.i(TAG, "No valid Google Play Services APK found.");
        }
    }


    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the app versionCode in the application's
     * shared preferences.
     */
    private void registerInBackground() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP, so it
                    // can use GCM/HTTP or CCS to send messages to your app.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device will send
                    // upstream messages to a server that echo back the message using the
                    // 'from' address in the message.

                    // Persist the regID - no need to register again.
                    data.saveData(PROPERTY_REG_ID, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.

                }
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                Toaster.toast("GCM Status  " + msg);
            }
        }.execute(null, null, null);
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                //GooglePlayServicesUtil.getErrorDialog(resultCode, context,PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");

            }
            return false;
        }
        return true;
    }

    private String getRegistrationId(Context context) {

        String registrationId = data.loadData(PROPERTY_REG_ID);
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    /**
     * Sends the registration ID to your server over HTTP, so it can use GCM/HTTP or CCS to send
     * messages to your app. Not needed for this demo since the device sends upstream messages
     * to a server that echoes back the message using the 'from' address in the message.
     */
    private void sendRegistrationIdToBackend() {
        // Your implementation here.

        RequestQueue queue = VolleyHelper.getRequestQueue();

        final String gcmTransmitUrl = "";//Constants.GCM_REGISTER_URL;
        final String deviceID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        data.saveData("gcm_dev_id",deviceID);
        data.saveData("gcm_reg_id",regid);

        StringRequest myReq = new StringRequest(Request.Method.POST,
                gcmTransmitUrl,
                gcmTransmitSuccessListener(),
                gcmTransmitErrorListener()) {

            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("dev_id", deviceID);
                params.put("reg_id", regid);
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Token "+data.getAccessToken());
                return params;
            }
        };

        // Add the request to the RequestQueue.
        queue.add(myReq);
    }

    private Response.Listener<String> gcmTransmitSuccessListener() {
        return new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                Log.i(TAG, "response" + response);
                try {
                    JSONObject obj = new JSONObject(response);
                    boolean success = obj.getBoolean("success");
                    if(success)
                    {
                        Toaster.toast("GCm Success " + response);
                    }else
                    {
                        Toaster.toast("GCM Failed " + response);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e(TAG, "Could not parse malformed JSON: \"" + response + "\"");
                }
            }
        };
    }

    private Response.ErrorListener gcmTransmitErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "Error" + error.getMessage());

                Toaster.toast(
                        "Server Communication Failure Gcm Handler");
            }
        };
    }
}
