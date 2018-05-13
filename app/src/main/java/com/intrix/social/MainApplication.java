package com.intrix.social;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.FacebookSdk;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.intrix.social.common.GcmHandler;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.VolleyHelper;
import com.karumi.dexter.Dexter;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.exceptions.RealmMigrationNeededException;

/**
 * Created by sutharsha on 14/10/15.
 */
public class MainApplication extends Application {

    private static final String TAG = "MainApplication";

    public static Data data;
    GcmHandler gcmHandler;
    public static GoogleAnalytics analytics;
    public static Tracker tracker;
    public static MainApplication instance;


    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        RealmConfiguration realmConfiguration = buildDatabase();
        Realm.setDefaultConfiguration(realmConfiguration);

        data = new Data(this);
        data.initializeData();
        data.startSync(this);


        FacebookSdk.sdkInitialize(this);

//        LayerClient layerClient = LayerClient.newInstance(this, "layer:///apps/staging/e0b6ca1c-e28a-11e5-bf8b-0c6f890307be",
//                new LayerClient.Options().googleCloudMessagingSenderId(Constants.Sender_ID));
//
//        LayerClient.applicationCreated(this);
//
//        layerClient.registerConnectionListener(this)
//        layerClient.registerAuthenticationListener(this);


        Toaster.setContext(this);
        VolleyHelper.init(this);
        Dexter.initialize(this);


//        CitrusClient.getInstance(this).init(
//                "efu41awbd4-signup",
//                "0fdda55d06630e798aa3c17a3031e1fe",
//                "efu41awbd4-signin",
//                "e549b99f0321e2aab2aa8b8387d709ed",
//                "efu41awbd4",
//                Environment.SANDBOX
//        );

        gcmHandler = new GcmHandler(this);
        //Log.i(TAG, data.getUserName());


//        analytics = GoogleAnalytics.getInstance(this);
//        analytics.setLocalDispatchPeriod(1800);

        //tracker = analytics.newTracker("UA-65007268-1"); // Replace with actual tracker/property Id
        //tracker = analytics.newTracker("UA-64811203-2");
//        tracker.enableExceptionReporting(true);
//        tracker.enableAdvertisingIdCollection(false);
//        tracker.enableAutoActivityTracking(true);

    }

    public void track(String screenName, String category, String action, String label) {
        tracker.setScreenName(screenName);
        tracker.send(new HitBuilders.EventBuilder()
                .setCategory(category)
                .setAction(action)
                .setLabel(label)
                .build());
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public void tablePing() {
        Log.i(TAG, "Hola bugger");
        EventBus.getDefault().post(new NetworkEvent("tableNotification", true));
    }

    public RealmConfiguration buildDatabase(){
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this).build();

        try {
             Realm.getInstance(realmConfiguration);
            return realmConfiguration;
        } catch (RealmMigrationNeededException e){
            try {
                Realm.deleteRealm(realmConfiguration);
                //Realm file has been deleted.
                //return Realm.getInstance(realmConfiguration);
                return realmConfiguration;
            } catch (Exception ex){
                throw ex;
                //No Realm file to remove.
            }
        }
    }

    static void refreshAppData(Context context)
    {
        data = new Data(context);
        data.initializeData();
    }

}

