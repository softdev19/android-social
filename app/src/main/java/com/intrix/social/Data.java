package com.intrix.social;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intrix.social.model.Cart;
import com.intrix.social.model.Category;
import com.intrix.social.model.Connection;
import com.intrix.social.model.Customer;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.Customization;
import com.intrix.social.model.Event;
import com.intrix.social.model.FeedbackItem;
import com.intrix.social.model.Item;
import com.intrix.social.model.OrderData;
import com.intrix.social.model.Transaction;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.NetworkConfigs;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.SocialService;
import com.intrix.social.networking.UserPhotoUploadService;
import com.intrix.social.networking.model.App;
import com.intrix.social.networking.model.Chat;

import java.io.File;
import java.util.List;
import java.util.Random;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmObject;
import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by sutharsha on 08/10/15.
 */
public class Data {

    public final String TAG = "Data";
    static MainApplication mainApp;
    private Context context;
    Random rnd;
    public static final String saveTag = "SocialDATA";
    public String profilePicUrl = "http://lorempixel.com/48/48";
    private Networker mNetworker;
    private Realm mRealm;
    public List<CustomerMini> searchResult;
    public List<CustomerMini> rankResult;
    public List<CustomerMini> tagResult;
    public List<FeedbackItem> feedbackItems;
    public List<App> socialApps;
    public CustomerMini selectedCustomer = null;
    public List<Customer> otherCustomers = null;
    public List<Transaction> transactions = null;
    public Customer user;
    public String userSearchTerm = "";
    //public String newChat = "";
    public Chat newChat = null;
    public Chat selectedChat = null;
    public List<OrderData> openOrders;
    public int currentSelectedItemFeedback = 0;

    public Data(Context cont) {
        context = cont;
        mainApp = (MainApplication) cont;
        rnd = new Random();
        mNetworker = Networker.getInstance();
        mRealm = Realm.getDefaultInstance();
        //searchResult = null;
    }

    public void initializeData()
    {
        if(getCustomerId().length() > 0) {
            refreshUser();
            Cart.instance().restoreCurrentOrder();
            Cart.instance().restoreCurrentOrderConfirmed();
            Cart.instance().restoreTags();
            Cart.instance().restoreCartData();

        }
    }

    public String loadData(String name) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        return sp.getString(name, "");
    }

    public void saveData(String name, String value) {
        SharedPreferences sp = PreferenceManager
                .getDefaultSharedPreferences(context);
        sp.edit().putString(name, value).commit();
    }

    public boolean loadBooleanData(String name) {
        SharedPreferences prefs = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE);
        return prefs.getBoolean(name, false);
    }

    public void saveData(String name, Boolean value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putBoolean(name, value);
        editor.commit();
    }

    public void saveIntData(String name, int value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putInt(name, value);
        editor.commit();
    }

    public int loadIntData(String name) {
        SharedPreferences prefs = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE);
        int restoredInt = prefs.getInt(name, 0);
        return restoredInt;
    }

    public void saveLongData(String name, long value) {
        SharedPreferences.Editor editor = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE).edit();
        editor.putLong(name, value);
        editor.commit();
    }

    public long loadLongData(String name) {
        SharedPreferences prefs = context.getSharedPreferences(saveTag, Context.MODE_PRIVATE);
        long restoredInt = prefs.getLong(name, 0);
        return restoredInt;
    }


    public void startSync(Context context) {

        Gson gson = new GsonBuilder()
                .setExclusionStrategies(new ExclusionStrategy() {
                    @Override
                    public boolean shouldSkipField(FieldAttributes f) {
                        return f.getDeclaringClass().equals(RealmObject.class);
                    }

                    @Override
                    public boolean shouldSkipClass(Class<?> clazz) {
                        return false;
                    }
                })
                .create();

        SocialService service = new RestAdapter.Builder()
                .setEndpoint(NetworkConfigs.SOCIAL_API_URL)
                .setConverter(new GsonConverter(gson))
                .build().create(SocialService.class);

        service.getCategories(new RealmSyncCallback<Category>(context));
        service.getEvents(new RealmSyncCallback<Event>(context));
        service.getCustomizations(new RealmSyncCallback<Customization>(context));
        service.getItems(new RealmSyncCallback<Item>(context));
        service.getCustomers(new RealmSyncCallback<Customer>(context, "getCustomers"));
        if(user != null && user.getId() > 0)
            service.getConnections(user.getId(), new RealmSyncCallback<Connection>(context, "getConnections"));
        //service.getChatsSync(Integer.parseInt(getCustomerId()),  new RealmSyncCallback<Chat>(context));
    }

    static class RealmSyncCallback<T extends RealmObject> implements Callback<List<T>> {

        private static final String LOG_TAG = RealmSyncCallback.class.getSimpleName();

        private Realm mRealm;

        private String callName = "";

        RealmSyncCallback(Context context) {
            mRealm = Realm.getInstance(context);
        }

        RealmSyncCallback(Context context, String networkCall) {
            mRealm = Realm.getInstance(context);
            callName = networkCall;
        }


        @Override
        public void success(List<T> realmObjects, Response response) {
            Log.d(LOG_TAG, "Got response from server: " + response.getStatus());
            Log.i(LOG_TAG, realmObjects.toString());

//            if(realmObjects.get(0).getClass() == Customer.class)
//            {
//                for(Customer customer : (List<Customer>)realmObjects)
//                {
//                    Log.i(LOG_TAG, " ---+++---  " + customer.getId() + "  " + customer.getEmail());
//                }
//            }

            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(realmObjects);
            mRealm.commitTransaction();
            mRealm.close();

            if(callName.length() >0)
            if (response.getStatus() >= 200 && response.getStatus() <= 205) {
                EventBus.getDefault().post(new NetworkEvent(callName, true));
            } else
                EventBus.getDefault().post(new NetworkEvent(callName, false));


        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(LOG_TAG, "Error when syncing with server: " + error.getMessage(), error);
            if(callName.length() >0)
            EventBus.getDefault().post(new NetworkEvent(callName, false));
        }
    }

    public String getChannelId() {
        return loadData("channel.id");
    }
    public String getUserId() {
        return loadData("user.id");
    }
    public String getAccessToken() {
        return loadData("serverAccessToken");
    }
//    public String getUserName() {
//        return loadData("userName");
//    }
    public String getFirstName() {
        return loadData("first_name");
    }
    public String getLastName() {
        return loadData("last_name");
    }
    public String getMobile() {
        return loadData("user.mobile");
    }

    public String getLatLong() {
        return loadData("latitude") + " , " + loadData("longitude");
    }

    public String getAddress() {
        return loadData("address");
    }

    public String getUserAddress() {
        return loadData("user.address");
    }
    public String getEmail() {
        return loadData("user.email");
    }
    public String getLocation() {
        return loadData("user.location");
    }

    public String getSignUpType()
    {
        return loadData("signup.type");
    }

    public String getCustomerId() {
        return loadData("user.customerId");
    }

    // depreceated
    public String getUserName() {
       return loadData("user.name");
    }

    public String getName() {
        return loadData("user.name");
    }


    public String getDownVotes() {
        return loadData("user.downvotes");
    }

    public String getUpVotes() {
        return loadData("user.upvotes");
    }

    public int getSocialCurrency() {
        return loadIntData("user.socialcurrency");
    }


    public void setSocialCurrency(int value) {
        saveIntData("user.socialcurrency", value);
    }


    public boolean getLoginStatus()
    {
        return loadBooleanData("login.status");
    }

    public String getProfilePicUrl() {
        return loadData("user.pic");
    }

    public String getUserTags() {

        String fullTags = "";
        if(loadData("user.love1").length() >0)
            fullTags = fullTags + " #"+loadData("user.love1");

        if(loadData("user.love2").length() >0)
            fullTags = fullTags + " #"+loadData("user.love2");

        if(loadData("user.love3").length() >0)
            fullTags = fullTags + " #"+loadData("user.love3");

        return fullTags;
    }

    Uri photoLocalUri = null;
    public File newProfileImage = null;
    public File ProfileImage = null;
    public void savePhoto(Uri photoUri, boolean camera, boolean original) {
        //saveData("user.pic",photoUri.toString());
        photoLocalUri =  photoUri;
        Log.i(TAG, "photo uri " + photoUri.toString());
        Intent intent = new Intent(MainApplication.instance, UserPhotoUploadService.class);
        intent.putExtra(UserPhotoUploadService.PHOTO_URI, photoUri);
        intent.putExtra(UserPhotoUploadService.IS_CAMERA_PHOTO, camera);
        intent.putExtra("original", original);
        MainApplication.instance.startService(intent);
    }

    public void refreshUser()
    {
        if(user == null)
            user = new Customer();
        user.setId(Integer.parseInt(getCustomerId()));
        user.setDescription(loadData("user.description"));
        user.setInterest1(loadData("user.love1"));
        user.setInterest2(loadData("user.love2"));
        user.setInterest3(loadData("user.love3"));

        user.setFblink(loadData("user.fblink"));
        user.setTwtlink(loadData("user.twtlink"));
        user.setBelink(loadData("user.belink"));
        user.setDrlink(loadData("user.drlink"));
        user.setSclink(loadData("user.sclink"));
        user.setOtherlink(loadData("user.otherlinks"));

        user.setDescription2(loadData("user.description2"));
        user.setDescription3(loadData("user.description3"));
        user.setMobileno(getMobile());
        user.setPic(getProfilePicUrl());
        user.setLocation(loadData("user.location"));
        user.setName(getUserName());
        user.setUpvotes(loadData("user.upvotes"));
        user.setDownvotes(loadData("user.downvotes"));
        Log.i(TAG, "User data refreshed - " +user.getId()+ " - "+user.getName());
    }
}
