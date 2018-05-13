package com.intrix.social.layer;

import android.util.Log;

import com.layer.sdk.LayerClient;
import com.layer.sdk.exceptions.LayerException;
import com.layer.sdk.listeners.LayerConnectionListener;

public class MyConnectionListener implements LayerConnectionListener {

    private static final String TAG = MyConnectionListener.class.getSimpleName();

    private SocialWall main_activity;

    public MyConnectionListener(SocialWall ma) {
        //Cache off the main activity in order to perform callbacks
        main_activity = ma;
    }

    //Called on connection success. The Quick Start App immediately tries to
    //authenticate a user (or, if a user is already authenticated, return to the conversation
    //screen).
    public void onConnectionConnected(LayerClient client) {
        Log.v(TAG, "Connected to Layer");

        //If the user is already authenticated (and this connection was being established after
        // the app was disconnected from the network), then start the conversation view.
        //Otherwise, start the authentication process, which effectively "logs in" a user
        if (client.isAuthenticated())
            main_activity.onUserAuthenticated();
        else
            client.authenticate();

    }

    //Called when the connection is closed
    public void onConnectionDisconnected(LayerClient client) {
        Log.v(TAG, "Connection to Layer closed");
    }

    //Called when there is an error establishing a connection. There is no need to re-establish
    // the connection again by calling layerClient.connect() - the SDK will handle re-connection
    // automatically. However, this callback can be used with conjunction with onConnectionConnected
    // to provide feedback to the user that messages cannot be sent/received (assuming there is an
    // authenticated user).
    public void onConnectionError(LayerClient client, LayerException e) {
        Log.v(TAG, "Error connecting to layer: " + e.toString());
    }
}