/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intrix.social.gcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GcmListenerService;
import com.intrix.social.MainActivity;
import com.intrix.social.MainApplication;
import com.intrix.social.PeopleDetailActivity;
import com.intrix.social.R;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Connection;
import com.intrix.social.model.Item;
import com.intrix.social.networking.Networker;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import io.realm.Realm;

public class MyGcmListenerService extends GcmListenerService {

    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {

        Log.d(TAG, "Something recieved");

        String message = data.getString("message");
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "All: " + data);

        //collapse_key
        //String type = data.getString("type");
        String type = data.getString("type");

        switch (type)
        {
            case "table alloted":
                processTable(data);
                break;
            case "tag":
                processTag(data);
                break;
            case "split":
                processSplit(data);
                break;
            case "settlement":
                processSettlement(data);
                break;
            case "connect":
                processConnect(data);
                break;
            case "table accepted":
                processTableAccepted(data);
                break;
            case "table denied":
                processTableDenied(data);
                break;
            case "item added":
                processItemOrdered(data);
                break;
            case "order cancelled":
                processOrderCancelled(data);
        }
    }

    // [END receive_message]

    /**
     * Create and show a simple notification containing the received GCM message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Social:")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    //{tagged_by=112, tagged_by_name=Nikhil Warrier, table_id=89, tagged_by_image=https://graph.facebook.com/10153598453612933/picture?type=large, type=tag, order_id=262, collapse_key=tagging}]
    private void sendNotificationTagged(Bundle data) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);


        String imageUrl = data.getString("tagged_by_image");
        Bitmap image = getBitmapFromURL(imageUrl);
        String message = "You have been tagged to "+data.getString("tagged_by_name")+"'s table";
        //String message = "You have been tagged";

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Social:")
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(defaultSoundUri)
                .setLargeIcon(image)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    public Bitmap getBitmapFromURL(String strURL) {
        try {
            URL url = new URL(strURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
//{table_code=56, table_id=88, type=table alloted, collapse_key=Table Id}
    private void processTable(Bundle data)
    {
        int tableId = Integer.parseInt(data.getString("table_id"));
        //int tableCode = Integer.parseInt(data.getString("table_code"));
        Cart.instance().setTableNo(tableId);

        MainApplication.data.saveIntData("tableCode", tableId);
        String message = "You have been alloted table number "+tableId+"";
        sendNotification(message);
        Intent i = new Intent("tableNotification");
        sendBroadcast(i);
    }

    private void processTag(Bundle data)
    {
        int orderId = Integer.parseInt(data.getString("order_id"));
        int taggerId = Integer.parseInt(data.getString("tagged_by"));
        String taggerName = data.getString("tagged_by_name");
        int tableId = Integer.parseInt(data.getString("table_id"));
        String taggerImage = data.getString("tagged_by_image");

        Cart.instance().setTableNo(tableId);
        Cart.instance().setOrderId(orderId);
        MainApplication.data.saveIntData("tableCode", tableId);
        MainApplication.data.saveIntData("taggerId", taggerId);

        sendNotificationTagged(data); // send notification

        Intent i = new Intent("tagNotification");
        sendBroadcast(i);
    }

    private void processSplit(Bundle data)
    {
        int orderId = Integer.parseInt(data.getString("order_id"));
        if(Cart.instance().getOrderId() == orderId) {
            int customerId = Integer.parseInt(data.getString("customer_id"));
            int amount = Integer.parseInt(data.getString("amount"));
            String mode = data.getString("mode");

            Cart.instance().setSettlementType("split");
            //Cart.instance().setSettlementPendingAmount(amount);
            Cart.instance().setSplitAmount(amount);
            sendNotification("Settlement has been split up. Pending amount is Rs: " + amount);

            Intent i = new Intent("splitNotification");
            sendBroadcast(i);
        }
    }

    private void processSettlement(Bundle data)
    {
        int orderId = Integer.parseInt(data.getString("order_id"));
        int customerId = Integer.parseInt(data.getString("customer_id"));
        int waiterId = Integer.parseInt(data.getString("waiter_id"));

        Log.i(TAG, " Order id mine " + Cart.instance().getOrderId() +" - got - "+orderId+" - customer id " + MainApplication.data.user.getId() +" - "+customerId);
        if(Cart.instance().getOrderId() == orderId && customerId != MainApplication.data.user.getId()) {

            int amount = Integer.parseInt(data.getString("amount"));
            String mode = data.getString("mode");

            Cart.instance().setSettlementType("pending");

            Realm mRealm = Realm.getInstance(getApplicationContext());
            int subtotal  = Cart.instance().calculateFullOrderAmount(mRealm);
            mRealm.close();
            int taxes = (int) ((double) subtotal * 0.26);
            int currentAmount = subtotal + taxes;

            int currentCartAmount = Cart.instance().getSettlementPendingAmount();

            boolean tableReleased = false;
            if(currentCartAmount == 0)
                Cart.instance().setSettlementPendingAmount(currentAmount - amount);
            else {
                Cart.instance().setSettlementPendingAmount(currentCartAmount - amount);
                if((currentCartAmount - amount) == 0) {
                    Cart.instance().releaseTable();
                    Intent i = new Intent("tableReleased");
                    tableReleased = true;
                    sendBroadcast(i);
                }
            }

            if(Cart.instance().getSettlementType().equalsIgnoreCase("split")) {
                sendNotification("Settlement of " + amount + " will be made in your table. After this your pending amount will be Rs: " + Cart.instance().getSettledAmount());
            }else
            {
                if(waiterId > 0)
                    sendNotification("Settlement of " + amount + " has been made in your table by your waiter");
                else
                    sendNotification("Settlement of " + amount + " has been made in your table");
            }
            if(!tableReleased) {
                Intent i = new Intent("settlementNotification");
                sendBroadcast(i);
            }
        }else
        {

        }

    }
//{customer_name=Sutharshan Ram, customer_id=3, image_url=http://res.cloudinary.com/intrix/image/upload/v1457257893/dy0neak5gxxnvew3hlbc.jpg, id=1, type=connect, collapse_key=connect_request}]
    private void processConnect(Bundle data)
    {

        String name = data.getString("customer_name");
        String imageUrl = data.getString("image_url");
        Bitmap image = getBitmapFromURL(imageUrl);
        String message = name + " has sent you a connect request";
        int connectRequestId = Integer.parseInt(data.getString("id"));

        Realm mRealm = Realm.getInstance(getApplicationContext());
        Connection connection = mRealm.where(Connection.class).equalTo("id", connectRequestId).findFirst();
        if(connection == null)
            Networker.getInstance().getConnectionsAuto();
        sendNotificationConnectRequest(data);
    }

    private void sendNotificationConnectRequest(Bundle data) {
        Intent intent = new Intent(this, PeopleDetailActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String name = data.getString("customer_name");
        String imageUrl = data.getString("image_url");
        Bitmap image = getBitmapFromURL(imageUrl);
        String message = name + "has sent you a connect request";
        int connectRequestId = Integer.parseInt(data.getString("id"));


        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Social:")
                .setContentText(message)
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSound(defaultSoundUri)
                .setLargeIcon(image)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void processTableAccepted(Bundle data)
    {
        int tableId = Integer.parseInt(data.getString("table_id"));
        int tableCode = Integer.parseInt(data.getString("table_code"));
        //Networker.getInstance().getTablesRequested();
        String message = "Table "+tableId+" request has been Accepted.";
        sendNotification(message);
        Intent i = new Intent("tableAcceptedNotification");
        sendBroadcast(i);
    }

    private void processTableDenied(Bundle data)
    {
        int tableId = Integer.parseInt(data.getString("table_id"));
        int tableCode = Integer.parseInt(data.getString("table_code"));
        String message = "Table "+tableId+" request has been Denied.";
        sendNotification(message);
        Intent i = new Intent("tableDeniedNotification");
        sendBroadcast(i);
    }

    private void processItemOrdered(Bundle data)
    {
        int tableId = Integer.parseInt(data.getString("table_no"));
        int itemId = Integer.parseInt(data.getString("item"));

        Realm mRealm = Realm.getInstance(getApplicationContext());
        Item orderedItem = mRealm.where(Item.class).equalTo("id", itemId).findFirst();

        String message = orderedItem.getName()+ " ordered by your waiter";
        sendNotification(message);

        Intent i = new Intent("itemOrderedNotification");
        sendBroadcast(i);
    }

    private void processOrderCancelled(Bundle data)
    {

        Cart.instance().clearAll();
        String message = " Your table has been cancelled by your waiter";
        sendNotification(message);

        Intent i = new Intent("orderCancelled");
        sendBroadcast(i);
    }

    //options = {data: { type: "order cancelled"}, collapse_key: "order cancelled"}
}
