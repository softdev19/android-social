/*
 * This Quick Start App is designed to get you up and running as quickly as possible with the
 *  Layer SDK. There are no frills in this app, which is designed to be run on a Device and Simulator
 *  and starts a conversation between the two.
 *
 *  Key Features
 *   - Start a single conversation between a physical device ("Device") and Emulator ("Simulator")
 *   - The Device will support Push Notifications if tied to a properly configured Google Project
 *   - Functionality includes: Connecting to Layer, Authenticating a User, Running a Query, Creating
 *     a New Conversation, Sending Text Messages, Typing Indicators, Delivery/Read Receipts, Event
 *     Change Listeners, and Sync Listeners
 *   - Works cross platform with the iOS Quick Start App
 *
 *  Setup
 *   - Replace the "LAYER_APP_ID" with the Staging App ID in the Layer Dashboard (under the "Keys"
 *     tab)
 *   - Optional: Replace "GCM_Project_Number" with a correctly configured Google Project Number in
 *     order to support Push
 *   - Launch the App on both a Device and a Simulator to start a conversation
 *
 */

package com.intrix.social.layer;

import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.common.Constants;
import com.layer.sdk.LayerClient;

import java.util.Arrays;
import java.util.List;

public class SocialWall extends AppCompatActivity {

    //Replace this with your App ID from the Layer Developer page.
    //Go http://developer.layer.com, click on "Dashboard" and select "Keys"
    public static final String LAYER_APP_ID = "layer:///apps/staging/e0b6ca1c-e28a-11e5-bf8b-0c6f890307be";

    //Optional: Enable Push Notifications
    // Layer uses Google Cloud Messaging for Push Notifications. Go to
    // https://developer.layer.com/docs/guides/android#push-notification
    // and follow the guide to configure a Google Project. If the default or
    // an invalid Project Number is used here, the Layer SDK will function, but
    // users will not receive Notifications when the app is closed or in the
    // background).
    public static final String GCM_PROJECT_NUMBER = Constants.Sender_ID;//"00000";

    //Global variables used to manage the Layer Client and the conversations in this app
    private LayerClient layerClient;
    private ConversationViewController conversationView;

    //Layer connection and authentication callback listeners
    private MyConnectionListener connectionListener;
    private MyAuthenticationListener authenticationListener;

    //onCreate is called on App Start
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //If we haven't created a LayerClient, show the loading splash screen
        if(layerClient == null)
            setContentView(R.layout.activity_social_wall);


        //Create the callback listeners

        if(connectionListener == null)
            connectionListener = new MyConnectionListener(this);

        if(authenticationListener == null)
            authenticationListener = new MyAuthenticationListener(this);
    }

    //onResume is called on App Start and when the app is brought to the foreground
    protected void onResume(){
        super.onResume();

        //Connect to Layer and Authenticate a user
        loadLayerClient();

        //Every time the app is brought to the foreground, register the typing indicator
        if(layerClient != null && conversationView != null)
            layerClient.registerTypingIndicator(conversationView);
    }

    //onPause is called when the app is sent to the background
    protected void onPause(){
        super.onPause();

        //When the app is moved to the background, unregister the typing indicator
        if(layerClient != null && conversationView != null)
            layerClient.unregisterTypingIndicator(conversationView);
    }

    //Checks to see if the SDK is connected to Layer and whether a user is authenticated
    //The respective callbacks are executed in MyConnectionListener and MyAuthenticationListener
    private void loadLayerClient(){

        // Check if Sample App is using a valid app ID.
        if (isValidAppID()) {

            if(layerClient == null){

                //Used for debugging purposes ONLY. DO NOT include this option in Production Builds.
                //LayerClient.setLoggingEnabled(this.getApplicationContext(),true);

                // Initializes a LayerClient object with the Google Project Number
                LayerClient.Options options = new LayerClient.Options();

                //Sets the GCM sender id allowing for push notifications
                options.googleCloudMessagingSenderId(GCM_PROJECT_NUMBER);

                //By default, only unread messages are synced after a user is authenticated, but you
                // can change that behavior to all messages or just the last message in a conversation
                options.historicSyncPolicy(LayerClient.Options.HistoricSyncPolicy.ALL_MESSAGES);


                layerClient = LayerClient.newInstance(this, LAYER_APP_ID, options);

                //Register the connection and authentication listeners
                layerClient.registerConnectionListener(connectionListener);
                layerClient.registerAuthenticationListener(authenticationListener);
            }

            //Check the current state of the SDK. The client must be CONNECTED and the user must
            // be AUTHENTICATED in order to send and receive messages. Note: it is possible to be
            // authenticated, but not connected, and vice versa, so it is a best practice to check
            // both states when your app launches or comes to the foreground.
            if (!layerClient.isConnected()) {

                //If Layer is not connected, make sure we connect in order to send/receive messages.
                // MyConnectionListener.java handles the callbacks associated with Connection, and
                // will start the Authentication process once the connection is established
                layerClient.connect();

            } else if (!layerClient.isAuthenticated()) {

                //If the client is already connected, try to authenticate a user on this device.
                // MyAuthenticationListener.java handles the callbacks associated with Authentication
                // and will start the Conversation View once the user is authenticated
                layerClient.authenticate();

            } else {

                // If the client is to Layer and the user is authenticated, start the Conversation
                // View. This will be called when the app moves from the background to the foreground,
                // for example.
                onUserAuthenticated();
            }
        }
    }

    //If you haven't replaced "LAYER_APP_ID" with your App ID, send a message
    private boolean isValidAppID() {
        if(LAYER_APP_ID.equalsIgnoreCase("LAYER_APP_ID")) {

            // Instantiate an AlertDialog.Builder with its constructor
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            // Chain together various setter methods to set the dialog characteristics
            builder.setMessage("To correctly use this project you need to replace LAYER_APP_ID in MainActivity.java (line 39) with your App ID from developer.layer.com.")
                    .setTitle(":-(");

            // Get the AlertDialog from create() and then show() it
            AlertDialog dialog = builder.create();
            dialog.show();

            return false;
        }

        return true;
    }

    //Layer is fairly flexible when it comes to User Management. You can use an existing system, or
    // create a new one, as long as all user ids are unique. For demonstration purposes, we are
    // making the assumption that this App will be run simultaneously on a Simulator and on a
    // Device, and assign the User ID based on the runtime environment.
    public static String getUserID(){
        if(Build.FINGERPRINT.startsWith("generic"))
            return "Simulator";

        return MainApplication.data.user.getEmail();
        //return "Device";
    }

    //By default, create a conversationView between these 3 participants
    public static List<String> getAllParticipants(){
        return Arrays.asList(MainApplication.data.user.getEmail(),"Device", "Simulator", "Dashboard");
    }

    //Once the user has successfully authenticated, begin the conversationView
    public void onUserAuthenticated(){

        if(conversationView == null) {

            conversationView = new ConversationViewController(this, layerClient);

            if (layerClient != null) {
                layerClient.registerTypingIndicator(conversationView);
            }
        }
    }
}
