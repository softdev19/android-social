package com.intrix.social.fragment;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Point;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.intrix.social.Data;
import com.intrix.social.MainApplication;
import com.intrix.social.PeopleActivity;
import com.intrix.social.R;
import com.intrix.social.ResultPeopleActivity;
import com.intrix.social.adapter.UsersAdapter;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Random;

import de.greenrobot.event.EventBus;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;
//import fr.castorflex.android.circularprogressbar.CircularProgressBar;

/**
 * Created by toanpv on 11/30/15.
 */
public class DiscoveringFragment extends Fragment implements View.OnClickListener {

    final private String TAG = "DiscoveringFragment";
    final private int userRadius = 25;
    final private int dotRadius = 10;
    final private int maxDiameter = 360;
    final private int maxRadius = 180 - userRadius;
    Data data;
    LayoutInflater li = null;
    View fragView = null;
    ListView listView;
    String interest = "";
    boolean interested = false;
    FloatingActionButton fab;
    boolean filterOn = false;
    ArrayList<Point> dotPoints = new ArrayList<>();
    ArrayList<View> dotViews = new ArrayList<>();
    float dpHeight = 0;


    //ArrayList<CircularProgressBar> circlesList = new ArrayList<>();
    float dpWidth = 0;
    private Runnable runnablePtr = null;
    private Handler handler = null;
    private Dialog mDialog;
    boolean gettingLocation = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discovering_people, container, false);
        li = inflater;
        fragView = v;

        EventBus.getDefault().register(this);
        data = MainApplication.data;
/*
        CircularProgressBar cp = (CircularProgressBar) fragView.findViewById(R.id.rp1);
        circlesList.add(cp);
        cp = (CircularProgressBar) fragView.findViewById(R.id.rp2);
        circlesList.add(cp);
        cp = (CircularProgressBar) fragView.findViewById(R.id.rp3);
        circlesList.add(cp);
        cp = (CircularProgressBar) fragView.findViewById(R.id.rp4);
        circlesList.add(cp);
        cp = (CircularProgressBar) fragView.findViewById(R.id.rp5);
        circlesList.add(cp);
*/
        fab = (FloatingActionButton) v.findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getActivity(), R.color.splash_accent)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(DiscoveringActivity.this, ResultPeopleActivity.class));
             /*   startActivity(new Intent(getActivity(), PeopleActivity.class));
                if (handler != null && runnablePtr != null)
                    handler.removeCallbacks(runnablePtr);
                */
            }
        });

//        TextView searchTextView = (TextView) v.findViewById(R.id.tv_tag_discover);
//        searchTextView.setText("");

        String searchTerm = "";

        if (data.userSearchTerm.length() > 0)
            searchTerm = data.userSearchTerm;
        else
            //searchTerm = data.loadData("user.love1");
            searchTerm = "";

//        String location = MainApplication.data.getLocation();
//        if (location.length() > 0)
//            searchTerm = location;

//        if(searchTerm.length() == 0)
//            searchTextView.setText("Searching.....");

        v.findViewById(R.id.home).setOnClickListener(this);

        // Networker.getInstance().searchCustomers(searchTerm);

        final String searchTermFinal = searchTerm;
        runnablePtr = new Runnable() {
            @Override
            public void run() {
                //startActivity(new Intent(DiscoveringActivity.this, ResultPeopleActivity.class));
//                if(MainApplication.data.selectedCustomer != null)
//                    startActivity(new Intent(getActivity(), PeopleActivity.class));
                if (interested) {
                    if (interest.length() > 0) {
                        showProgressDialog("Searching socialites by interest", false);
                        Networker.getInstance().searchCustomersByInterest(interest);
                    } else
                        Toaster.showToast("Your interests are blank");
                } else {
                    showProgressDialog("Searching for socialites around you", false);
                    if (searchTermFinal.length() > 0)
                        Networker.getInstance().searchCustomers(searchTermFinal);
                    else
                        Networker.getInstance().discover();
                }
            }
        };

        handler = new Handler();


        Button filterInterestBtn = (Button) v.findViewById(R.id.btn_filter_by_interest);
        filterInterestBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermissionAndUpdateLocation();
            }
        });

        listView = (ListView) v.findViewById(R.id.people_list);
        View empty = v.findViewById(android.R.id.empty);
        listView.setEmptyView(empty);
        listView.setVisibility(View.INVISIBLE);
        //checkLocation();
//        if(isGpsOn())
//            getGpsAndSearch();

        //processDiscover();
        requestPermissionAndUpdateLocation();

        //handler.postDelayed(runnablePtr, 0);
        return v;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
            if (ab != null) {
                ab.hide();
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
            if (ab != null && !ab.isShowing()) {
                ab.show();
            }
        }
    }

    public void onEvent(NetworkEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        if (event.event.contains("searchCustomers")) {
            hideProgress();
            backStackString = "searchCustomers";
            if (event.status) {
                Toaster.toast("searchCustomers - success", true);
//                Intent i = new Intent(getActivity(), UniversalActivity.class);
//                i.putExtra(UniversalActivity.EXTRA_TOKEN, PeopleResultFragment.class);
//                startActivity(i);

                if(MainApplication.data.searchResult != null && MainApplication.data.searchResult.size() >0) {
                    startActivity(new Intent(getActivity(), ResultPeopleActivity.class));
                }else
                    Toaster.showToast("Zero users found :(");

//          showUsers();
//          fab.setVisibility(View.INVISIBLE);
//          listView.setVisibility(View.VISIBLE);
//          setSearchResult();
            } else
                Toaster.toast("Unable to contact server. Please try again later", true);
        }


        if (event.event.contains("updateUserLocation")) {
            hideProgress();
            if (event.status) {
                search();
            } else
                Toaster.toast("Unable update user location", true);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.home) {
            Activity activity = getActivity();
            activity.onBackPressed();
//            if (activity instanceof UniversalActivity) {
//                ((MainActivity) activity).openDrawer();
//            }
        }
    }

    void showUsers() {
        final ArrayList<CustomerMini> arrayOfUsers = (ArrayList<CustomerMini>) MainApplication.data.searchResult;

        //Toaster.toast("Showing Users", true);
        //LayoutInflater li = getActivity().getLayoutInflater();
        View view = li.inflate(R.layout.discover_user, null);
        RelativeLayout holder = (RelativeLayout) getActivity().findViewById(R.id.discover_anim_holder);
        Random rnd = new Random();

        int maxDots = arrayOfUsers.size();

        if (maxDots > 10)
            maxDots = 8;
        for (int i = 0; i < maxDots; i++) {
//            View dots = li.inflate(R.layout.discover_dot, null);
//            RelativeLayout.LayoutParams paramsI = new RelativeLayout.LayoutParams(
//                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            paramsI.addRule(RelativeLayout.CENTER_IN_PARENT);
//            holder.addView(dots, paramsI);
//            dots.animate().x(i * 20).y(i * 20);

            float r = maxRadius / 5 * i + 1;
            View dot = li.inflate(R.layout.discover_user, null);
            holder.addView(dot);
            RelativeLayout.LayoutParams dotParams =
                    (RelativeLayout.LayoutParams) dot.getLayoutParams();
            dotParams.addRule(RelativeLayout.CENTER_IN_PARENT);
            dot.setLayoutParams(dotParams);
            Point p = genPoint();
            dotPoints.add(p);
            p = new Point(p.x, p.y);
            Log.i(TAG, "after -" + p.toString());
            dot.setTranslationX(Utils.dpToPx(p.x));
            dot.setTranslationY(Utils.dpToPx(p.y));

            ImageView pic = (ImageView) dot.findViewById(R.id.dot_pic);
            CustomerMini temp = arrayOfUsers.get(i);
            dot.setTag(i);
            Glide.with(getActivity()).load(temp.getPic()).error(R.drawable.no_image).into(pic);
            dot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    data.selectedCustomer = arrayOfUsers.get((int) v.getTag());
                    startActivity(new Intent(getActivity(), PeopleActivity.class));
                }
            });
            dotViews.add(dot);
        }

//        for(CircularProgressBar cp : circlesList)
//        {
//            cp.setIndeterminate(false);
//        }
    }

    void initDp() {
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);

        float density = getResources().getDisplayMetrics().density;
        dpHeight = outMetrics.heightPixels / density;
        dpWidth = outMetrics.widthPixels / density;
    }

    private Point genPoint() {
        Random rnd = new Random(System.currentTimeMillis());

        //double radius = rnd.nextDouble() * maxRadius;
        double x = 0;
        double y = 0;

        int i = 0;
        do {
            double radius = Math.sqrt(rnd.nextDouble()) * maxRadius;  // uniform distribution
            if (radius < 55) {
                Log.i(TAG, "blackhole");
                continue;
            }
            double angle = rnd.nextDouble() * Math.PI * 2;
            x = radius * Math.cos(angle);
            y = radius * Math.sin(angle);
            Log.i(TAG, "try - " + i++ + " - r " + radius + " - x " + x + " - y " + y);
        } while (!isPointOk(x, y));
        return new Point((int) x, (int) y);
    }

    boolean isPointOk(double x, double y) {
        if (!isOverLapping(new Point((int) x, (int) y)) && x != 0 && y != 0)
            return true;
        else
            return false;
    }

    boolean isOverLapping(Point p1) {
        for (Point p : dotPoints) {
            if (distance(p, p1) < 50) {
                Log.i(TAG, "overlap");
                return true;
            }
        }
        return false;
    }

    double distance(Point p1, Point p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    void setSearchResult() {
        final ArrayList<CustomerMini> arrayOfUsers = (ArrayList<CustomerMini>) MainApplication.data.searchResult;
        UsersAdapter adapter = new UsersAdapter(getActivity(), arrayOfUsers);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                data.selectedCustomer = arrayOfUsers.get(position);
                startActivity(new Intent(getActivity(), PeopleActivity.class));
            }
        });
    }

    private void showProgressDialog(String message, boolean cancelable) {
        if (mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(getActivity());
        if(!cancelable)
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
        mDialog.setOnCancelListener(dialog -> {
            Log.i(TAG, " location process cancelled");
            Snackbar.make(fragView, "Discovering cancelled", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            gettingLocation = false;
        });
        //mDialog.setOnDismissListener();
    }

    private void hideProgress() {
        if (mDialog != null)
            mDialog.dismiss();
    }

    boolean isGpsUpdateRecent() {
        long nowTime = System.currentTimeMillis();
        long prevGpsTime = data.loadLongData("user.gps_time");
        if ((nowTime - prevGpsTime) > (60 * 60000)) {
            return true;
        } else
            return false;

    }

    boolean isGpsOn()
    {
        return SmartLocation.with(getActivity()).location().state().locationServicesEnabled();
    }

    void getGpsAndSearch()
    {

        //if(SmartLocation.with(getActivity()).location().state().locationServicesEnabled())


        gettingLocation = true;
        showProgressDialog("Getting your location", true);
        Log.i(TAG, "hello location state - " + SmartLocation.with(getActivity()).location().state().locationServicesEnabled());
        SmartLocation.with(getActivity()).location()
                .oneFix()
                .start(new OnLocationUpdatedListener() {

                    @Override
                    public void onLocationUpdated(Location location) {
                        Log.i(TAG, "hello OnLocationUpdatedListener ");
                        Log.i(TAG, "hello location new - " + location.getLatitude());
                        Log.i(TAG, "hello location new - " + location.getLongitude());

                        if(gettingLocation) {
                            showProgressDialog("Updating your location", false);
                            Networker.getInstance().setUserLocation(location.getLatitude(), location.getLongitude());
                        }
                        gettingLocation = false;
                    }
                });


        //SmartLocation.with(this).location().
        //SmartLocation.with(this).location().getLastLocation().getElapsedRealtimeNanos()
        /*
        Location location = SmartLocation.with(getActivity()).location().getLastLocation();
        if (location != null) {
            Log.i(TAG, "hello location 11 - " + location.getLatitude());
            Log.i(TAG, "hello location 11- " + location.getLongitude());

            if (location.getLatitude() > 0 || location.getLongitude() > 0)
                Networker.getInstance().setUserLocation(location.getLatitude(), location.getLongitude());
        }
        */
    }

    void search()
    {
        if (data.user.getInterest1().length() != 0)
            interest = data.user.getInterest1();
        else if (data.user.getInterest2().length() != 0)
            interest = data.user.getInterest2();
        else if (data.user.getInterest3().length() != 0)
            interest = data.user.getInterest3();
        else {
            interest = "";
            Toaster.toast("Your intersts seem to be blank");
            return;
        }

        fab.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

            /*    for(CircularProgressBar cp : circlesList)
                {
                    cp.setIndeterminate(true);
                }
            */

        RelativeLayout holder = (RelativeLayout) getActivity().findViewById(R.id.discover_anim_holder);
        for (View dot : dotViews) {
            holder.removeView(dot);
        }

        dotViews.clear();
        dotPoints.clear();

        handler.removeCallbacks(runnablePtr);
        handler.postDelayed(runnablePtr, 0);
    }

    private void requestPermissionAndUpdateLocation() {

        Log.i(TAG, "DEXTERING around ");

        //Dexter.isRequestOngoing()

        Dexter.checkPermission(new PermissionListener() {
            @Override
            public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */
                processDiscover();                //Toaster.toast("Granted", true);
            }

            @Override
            public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */
                Snackbar.make(fragView, "Yo dude. Come on. How do you expect to discover people without knowing your location?", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */
                Snackbar.make(fragView, "Need access to location to proceed.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                token.continuePermissionRequest();
            }
        }, Manifest.permission.ACCESS_FINE_LOCATION);

    }

    void processDiscover()
    {
        if(!isGpsOn()) {
            Snackbar.make(fragView, "Turn on GPS to Discover socialites around you.", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }else
        {
            getGpsAndSearch();
        }
    }
}
