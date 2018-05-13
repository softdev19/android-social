package com.intrix.social;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.facebook.login.LoginManager;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.intrix.social.chat.abstractions.ActivityWithOverlay;
import com.intrix.social.chat.fragments.WallFragment;
import com.intrix.social.chat.networking.PhotoUploadBroadcastReceiver;
import com.intrix.social.common.BackPressListener;
import com.intrix.social.common.view.menu.AppMenu;
import com.intrix.social.common.view.menu.AppMenuBuilder;
import com.intrix.social.common.view.menu.AppMenuItem;
import com.intrix.social.fragment.CategoriesFragment;
import com.intrix.social.fragment.DamagesFragment;
import com.intrix.social.fragment.DiscoverFragment;
import com.intrix.social.fragment.EventsFragment;
import com.intrix.social.fragment.TransactionFragment;
import com.intrix.social.fragment.WelcomeFragment;
import com.intrix.social.fragment.WorkspaceInfoFragment;
import com.intrix.social.gcm.QuickstartPreferences;
import com.intrix.social.gcm.RegistrationIntentService;
import com.intrix.social.model.Cart;
import com.intrix.social.model.event.ChangeFragmentEvent;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.Toaster;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;

import de.greenrobot.event.EventBus;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity implements ActivityWithOverlay {

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;
    public static final String ARG_START_FRAGMENT = "arg_start_fragment";
    public static final String ARG_OPEN_FAB = "arg_open_fab";

    static final String ACTION_SCAN = "com.google.zxing.client.android.SCAN";
    private final String TAG = "MainActivity";
    View mainView;
    View menuHeader;
    private GoogleApiClient mGoogleApiClient;
    private TextView mTitle;
    private Networker mNetworker;

    private AppMenu mMenu;
    private ViewGroup mMenuPanel;

    private BackPressListener mBackPressListener;

    private BroadcastReceiver mBroadcastReceiver;
    private BroadcastReceiver mRegistrationBroadcastReceiver;

    public static boolean isOnScreen;
    private boolean mHideDelayed;
    private Data data;
    private BroadcastReceiver receiver;
    IntentFilter filter = new IntentFilter();
    static int backStackCount = 0;

    //alert dialog for downloadDialog
    private static AlertDialog showDialog(final Activity act, CharSequence title, CharSequence message, CharSequence buttonYes, CharSequence buttonNo) {
        AlertDialog.Builder downloadDialog = new AlertDialog.Builder(act);
        downloadDialog.setTitle(title);
        downloadDialog.setMessage(message);
        downloadDialog.setPositiveButton(buttonYes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
                Uri uri = Uri.parse("market://search?q=pname:" + "com.google.zxing.client.android");
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    act.startActivity(intent);
                } catch (ActivityNotFoundException anfe) {

                }
            }
        });
        downloadDialog.setNegativeButton(buttonNo, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        return downloadDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Main Activity Init ------ }}}}}}}");
        setContentView(R.layout.activity_main);
        if(MainApplication.data == null)
        {
            MainApplication.refreshAppData(getApplicationContext());
        }
        data = MainApplication.data;

        mNetworker = Networker.getInstance();
        mainView = findViewById(R.id.main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        mTitle = (TextView) toolbar.findViewById(android.R.id.title);
        mTitle.setText("Social");
        int position = 1;

        if(MainApplication.data.loadBooleanData("checkedIn")) {
            if((MainApplication.data.loadIntData("taggerId") > 0 && MainApplication.data.loadIntData("tableCode") > 0)  ||
                    (Cart.instance().getPosOrderId() > 0 && MainApplication.data.loadIntData("tableCode") > 0 && Cart.instance().getTableNo(this) > 0 && Cart.instance().getOrderId() > 0))
                position = 2;
            else
                position = 1;
        }else
            position = 1;
        if (getIntent().hasExtra(ARG_START_FRAGMENT)) {
            position = getIntent().getIntExtra(ARG_START_FRAGMENT, 1);
        }

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        int color = ContextCompat.getColor(this, R.color.s_orange);
        com.intrix.social.chat.utils.Utils.setStatusBarColor(this, color);
        mMenuPanel = (ViewGroup) findViewById(R.id.menu);
        mMenu = AppMenuBuilder.withContext(this)
                .addItems(
                        new AppMenuItem(getString(R.string.home), FontAwesome.Icon.faw_home),
                        new AppMenuItem(getString(R.string.menu), FontAwesome.Icon.faw_cutlery),
                        new AppMenuItem(getString(R.string.drink), FontAwesome.Icon.faw_glass),
                        new AppMenuItem(getString(R.string.discover), FontAwesome.Icon.faw_user),
                        new AppMenuItem(getString(R.string.bills), FontAwesome.Icon.faw_calculator),
                        new AppMenuItem(getString(R.string.transactions), FontAwesome.Icon.faw_pie_chart),
                        new AppMenuItem(getString(R.string.leaderboard), FontAwesome.Icon.faw_arrow_circle_o_up),
                        new AppMenuItem(getString(R.string.workspace), FontAwesome.Icon.faw_list),
                        new AppMenuItem(getString(R.string.events), FontAwesome.Icon.faw_feed),
                        new AppMenuItem(getString(R.string.wall), FontAwesome.Icon.faw_comment),
                        new AppMenuItem(getString(R.string.profile), FontAwesome.Icon.faw_user),
                        new AppMenuItem(getString(R.string.logout), FontAwesome.Icon.faw_sign_out),
                        new AppMenuItem(getString(R.string.wipe_data), FontAwesome.Icon.faw_ambulance)

                )
                .withListener(pos -> {
                    FragmentManager fm = getSupportFragmentManager();
                    Fragment fragment = null;
                    switch (pos) {
                        case 0:
                            return;
                        case 1:
                            fragment = new WelcomeFragment();
                            setTitle("");
                            break;
                        case 2:
                            fragment = CategoriesFragment.create("Food");
                            if(!tableNotBooked())
                            setTitle("Food");
                            break;
                        case 3:
                            fragment = CategoriesFragment.create("Drink");
                            if(!tableNotBooked())
                            setTitle("Drink");
                            break;
                        case 4:
                            fragment = new DiscoverFragment();
                            setTitle("");
                            break;
                        case 5:
                            fragment = new DamagesFragment();
                            setTitle("Damages");
                            break;
                        case 6:
                            fragment = new TransactionFragment();
                            setTitle("Transactions");
                            break;
                        case 7:
//                            fragment = new TablesFragment();
//                            setTitle("Tables");
                            startActivity(new Intent(MainActivity.this, LeaderBoardActivity.class));
                            break;
                        case 8:
                            fragment = new WorkspaceInfoFragment();
                            setTitle("");
                            break;
                        case 9:
                            fragment = new EventsFragment();
                            setTitle("Events");
                            break;
                        case 10:
                            Intent intent = new Intent(MainActivity.this, NoToolbarUniversalActivity.class);
                            intent.putExtra(NoToolbarUniversalActivity.EXTRA_TOKEN, WallFragment.class);
                            startActivity(intent);
//                            startActivity(new Intent(MainActivity.this, SocialWall.class));
                            break;
                        case 11:
                            startActivity(new Intent(MainActivity.this, PeopleDetailActivity.class));
                            break;
                        case 12:
                            MainApplication.data.saveData("login.status", false);
                            LoginManager.getInstance().logOut();
                            finish();
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                            break;
                        case 13:
                            Cart.instance().removeAllFromCart();
                            Cart.instance().releaseTable();
                            MainApplication.data.saveData("login.status", false);
                            LoginManager.getInstance().logOut();
                            finish();
                            startActivity(new Intent(MainActivity.this, SignInActivity.class));
                            break;
                    }
                    if (fragment != null) {
                        if((pos == 2 || pos == 3)  && tableNotBooked())
                            showUnBookedPopup();
                        else {
                            Fragment fragTemp = fragment;
                            new Handler().post(new Runnable() {
                                public void run() {
                                    FragmentManager fm = getSupportFragmentManager();
                                    android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
                                    ft.replace(R.id.content_frame, fragTemp);
                                    ft.addToBackStack("chumma"+backStackCount++);
                                    ft.commit();
                                    Toolbar toolbar1 = (Toolbar) findViewById(R.id.toolbar);
                                    setSupportActionBar(toolbar1);
                                }
                            });
//                        fm.beginTransaction()
//                                .replace(R.id.content_frame, fragment)
//                                .commit();
                        }
                    }
                    closeDrawer();
                })
                .withHeader(getHeader())
                .withGradient(R.drawable.menu_gradient)
                .withBg(R.drawable.menu)
                .bindTo(mMenuPanel);

        ab.setDisplayHomeAsUpEnabled(true);
        ab.setHomeAsUpIndicator(R.drawable.ic_action_image_dehaze);

        EventBus.getDefault().register(this);

        mMenu.setSelectionAtPosition(position);

//        //We need to go to tables
//        if (Cart.instance().getTableNo(this) == -1) {
//            EventBus.getDefault().post(new ChangeFragmentEvent(getClass(),
//                    new WelcomeFragment()
//            ));
//        }

        filter.addAction("tableReleased");
        filter.addAction("orderCancelled");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //String account_id = intent.getStringExtra("account_id");

                switch (intent.getAction()) {
                    case "tableReleased": {
                        EventBus.getDefault().post(new ChangePageRequest(com.intrix.social.common.AppMenu.HOME));
                    }
                    break;
                    case "orderCancelled": {
                        EventBus.getDefault().post(new ChangePageRequest(com.intrix.social.common.AppMenu.HOME));
                    }
                    break;
                }
            }
        };

        mBroadcastReceiver = PhotoUploadBroadcastReceiver.registerNewInstance(this, this);
        initGCM();
//        long gpsTime = System.currentTimeMillis();
//        long prevGpsTime = data.loadLongData("user.gps_time");
//        if((gpsTime - prevGpsTime) > (60 * 60000)) {
//            updateLocation();
//        }

        requestPermissionAndUpdateLocation();


    }

    private boolean tableNotBooked() {
        return Cart.instance().getOrderId() == -1;
        //return Cart.instance().getTableNo(this) == -1;
    }

    private void showUnBookedPopup()
    {
        new android.app.AlertDialog.Builder(this)
                .setTitle("Table Not Booked")
                .setMessage("Please try after booking a table")
                .setPositiveButton("OK", null)
                .show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                openDrawer();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    //dirty hack, I'm tired, can be changed to posting event by somebody who is not so tired
    public static boolean OPEN_FAB;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra(ARG_START_FRAGMENT)) {
            OPEN_FAB = intent.getBooleanExtra(ARG_OPEN_FAB, false);
            if(MainApplication.data.loadBooleanData("checkedIn")) {
                if((MainApplication.data.loadIntData("taggerId") > 0 && MainApplication.data.loadIntData("tableCode") > 0)  ||
                        (Cart.instance().getPosOrderId() > 0 && MainApplication.data.loadIntData("tableCode") > 0 && Cart.instance().getTableNo(this) > 0 && Cart.instance().getOrderId() > 0))
                    mMenu.setSelectionAtPosition(intent.getIntExtra(ARG_START_FRAGMENT, 2));
                else
                    mMenu.setSelectionAtPosition(intent.getIntExtra(ARG_START_FRAGMENT, 1));
            }else
                mMenu.setSelectionAtPosition(intent.getIntExtra(ARG_START_FRAGMENT, 1));
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle.setText(title);
    }

    @Override
    public void setTitle(int titleId) {
        mTitle.setText(titleId);
    }

    public void openDrawer() {
        mMenuPanel.setVisibility(View.VISIBLE);
    }

    public void closeDrawer() {
        mMenuPanel.setVisibility(View.GONE);
    }

    public boolean isDrawerOpened() {
        return mMenuPanel.getVisibility() == View.VISIBLE;
    }

    long backPressedTime = 0;
    @Override
    public void onBackPressed() {
        if (isDrawerOpened()) {
            closeDrawer();
        } else if (mBackPressListener != null) {
            if (!mBackPressListener.onBackPressed()) {
                if(getSupportFragmentManager().getBackStackEntryCount() == 1)
                {
                    long currentTime = System.currentTimeMillis();
                    if((currentTime - backPressedTime) > 5000) {
                        Snackbar.make(mainView, "Press Back again to exit app", Snackbar.LENGTH_SHORT)
                                .setAction("Action", null).show();
                        backPressedTime = System.currentTimeMillis();
                    }else {
                        backPressedTime = System.currentTimeMillis();
                        super.onBackPressed();
                    }
                }
            }
        } else {
            if(getSupportFragmentManager().getBackStackEntryCount() == 1)
            {
                long currentTime = System.currentTimeMillis();
                if((currentTime - backPressedTime) > 5000) {
                    Snackbar.make(mainView, "Press Back again to exit app", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                    backPressedTime = System.currentTimeMillis();
                }else {
                    backPressedTime = System.currentTimeMillis();
                    super.onBackPressed();
                    //finish();
                }
            }else
            {
                super.onBackPressed();
            }
        }
    }

    //product qr code mode
    public void scanQR(View v) {
        try {
            Intent intent = new Intent(this, DecoderActivity.class);
            intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
            startActivity(intent);
            startActivityForResult(intent, 0);
        } catch (ActivityNotFoundException anfe) {
            //on catch, show the download dialog
            showDialog(MainActivity.this, "No Scanner Found", "Download a scanner code activity?", "Yes", "No").show();
        }
    }

    public void onEvent(ChangeFragmentEvent event) {
        if (event.receiver == getClass()) {
//            getSupportFragmentManager().beginTransaction()
//                    .replace(R.id.content_frame, event.fragment)
//                    //.addToBackStack("chumma"+backStackCount++)
//                    .commit();
            FragmentManager fm = getSupportFragmentManager();
            android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.content_frame, event.fragment);
            ft.addToBackStack("chumma"+backStackCount++);
            ft.commit();
        }
    }

    public void onEvent(ChangePageRequest event) {
        if (mMenu != null) {
            mMenu.setSelectionAtPosition(event.position);
        }
    }

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("setGcmId")) {
            if (event.status) {
                data.saveData("gcmIdSet", true);
                Log.i(TAG,"GCM Success");
            } else {
                data.saveData("gcmIdSet", false);
                Toaster.toast("GCM failed");
                Log.i(TAG, "GCM failed");
            }
        }
    }


    private View getHeader() {
        LayoutInflater inflater = getLayoutInflater();
        View v = inflater.inflate(R.layout.view_menu_header, null);
        //v.findViewById(R.id.close)
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeDrawer();
            }
        });
        return v;
    }

//    //on ActivityResult method
//    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
//        if (requestCode == 0) {
//            if (resultCode == RESULT_OK) {
//                //get the extras that are returned from the intent
//                String contents = intent.getStringExtra("SCAN_RESULT");
//                String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
//                Toast toast = Toast.makeText(this, "Content:" + contents + " Format:" + format, Toast.LENGTH_LONG);
//                toast.show();
//            }
//        }
//    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if(MainApplication.data == null)
        {
            MainApplication.refreshAppData(getApplicationContext());
        }
        registerReceiver(receiver, filter);
        // Logs 'install' and 'app activate' App Events.
        //    AppEventsLogger.activateApp(this);
        isOnScreen = true;
        if (mHideDelayed) hideOverlay();
    }

    @Override
    protected void onPause() {
        isOnScreen = false;
        unregisterReceiver(receiver);
        // Logs 'app deactivate' App Event.
        //  AppEventsLogger.deactivateApp(this);
        super.onPause();
    }


    public void setBackPressListener(BackPressListener backPressListener) {
        mBackPressListener = backPressListener;
    }

    @Override
    public void placeFragmentToOverlay(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_global_overlay, fragment)
                .commit();
    }

    @Override
    public boolean hideOverlay() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment f = fm.findFragmentById(R.id.fragment_global_overlay);
        if (f != null) {
            fm.beginTransaction().remove(f).commit();
            return true;
        } else return false;
    }


    @Override
    public void hideOverlayDelayed() {
        if (isOnScreen) hideOverlay();
        else mHideDelayed = true;
    }

    private void initGCM()
    {
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                //mRegistrationProgressBar.setVisibility(ProgressBar.GONE);
                SharedPreferences sharedPreferences =
                        PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences
                        .getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                data.saveData("gcmIdSet", false);
                if (sentToken) {
                  //  mInformationTextView.setText(getString(R.string.gcm_send_message));
                } else {
                   // mInformationTextView.setText(getString(R.string.token_error_message));
                }
            }
        };
        //mInformationTextView = (TextView) findViewById(R.id.informationTextView);

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, PLAY_SERVICES_RESOLUTION_REQUEST)
                        .show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }

    public void updateLocation()
    {
//        LocationAsync runner = new LocationAsync(this);
//        //String sleepTime = time.getText().toString();
//        runner.execute();


        Log.i(TAG, "hello location state - " + SmartLocation.with(this).location().state().locationServicesEnabled());
        SmartLocation.with(this).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {

                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.i(TAG, "hello location - " + location.getLatitude());
                        Log.i(TAG, "hello location - " + location.getLongitude());
                        Networker.getInstance().setUserLocation(location.getLatitude(), location.getLongitude());
                    }
                });

        //SmartLocation.with(this).location().
        //SmartLocation.with(this).location().getLastLocation().getElapsedRealtimeNanos()
        Location location =  SmartLocation.with(this).location().getLastLocation();
        if(location != null) {
            Log.i(TAG, "hello location 11 - " + location.getLatitude());
            Log.i(TAG, "hello location 11- " + location.getLongitude());

            if(location.getLatitude() > 0 || location.getLongitude() > 0)
                Networker.getInstance().setUserLocation(location.getLatitude(), location.getLongitude());
        }

    }

    private void requestPermissionAndUpdateLocation() {
//        MultiplePermissionsListener dialogMultiplePermissionsListener =
//                DialogOnAnyDeniedMultiplePermissionsListener.Builder
//                        .withContext(this)
//                        .withTitle("Location access permission")
//                        .withMessage("We require your permission to use GPS")
//                        .withButtonText(android.R.string.ok)
//                        .build();
//
// dialogMultiplePermissionsListener.onPermissionsChecked();
        //Dexter.checkPermissions(dialogMultiplePermissionsListener,
          //      Manifest.permission.ACCESS_FINE_LOCATION);

        Dexter.checkPermission(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */
                updateLocation();
            //Toaster.toast("Granted", true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */
                Snackbar.make(mainView, "Unable to locate you due to prmission denial. :(. ", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */
                Snackbar.make(mainView, "Need access to location to proceed", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //requestPermissionAndUpdateLocation();
                //token.cancelPermissionRequest();
                token.continuePermissionRequest();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION);

    }
}
