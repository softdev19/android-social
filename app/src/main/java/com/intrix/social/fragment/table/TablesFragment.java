package com.intrix.social.fragment.table;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.intrix.social.MainActivity;
import com.intrix.social.R;
import com.intrix.social.adapter.FoundTablesAdapter;
import com.intrix.social.common.AppMenu;
import com.intrix.social.common.BackPressListener;
import com.intrix.social.fragment.OutletScreenFragment;
import com.intrix.social.model.Cart;
import com.intrix.social.model.CitiesResponse;
import com.intrix.social.model.event.ChangePageRequest;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.TablesResponse;
import com.intrix.social.utils.CurrentLocationNew;
import com.intrix.social.utils.Utils;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yarolegovich on 29.12.2015.
 */
public class TablesFragment extends Fragment implements ProceedListener<String>,
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener, View.OnClickListener,
        BackPressListener {

    private final String TAG = "TablesFragment";
    Location location;
    SupportMapFragment mapFragment;
    private CurrentLocationNew currentLocationNew;
    private double currentLon;
    private double currentLat;

    ArrayList<HashMap<String, String>> Al_cities = new ArrayList<HashMap<String, String>>();

    HashMap<String, String> hash_map;

    ArrayList<HashMap<String, Double>> Al_disatance = new ArrayList<HashMap<String, Double>>();

    HashMap<String, Double> hash_distance;

    Marker marker;
    private Spinner spin_cities_filiter;
    CitiesResponse data;
    String[] Cities = {"Mumbai", "Bangalore", "Delhi"};
    String state = "punjab";
    private Dialog mDialog;
    boolean bol_firsttime;

    boolean bol_firsttime_zoom;
    int zoom = 4;

    long startTime = 0;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_tables, container, false);

        startTime = System.currentTimeMillis();

        Log.i(TAG, " current " + System.currentTimeMillis());
        Log.i(TAG, " 0 - " + (System.currentTimeMillis() - startTime));
        mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Log.i(TAG, " 1 - " + (System.currentTimeMillis() - startTime));
        currentLocationNew = new CurrentLocationNew(getActivity());
        currentLon = currentLocationNew.getLongitude();
        currentLat = currentLocationNew.getLatitude();

        /*
        Log.i(TAG, " 2 - " + (System.currentTimeMillis() -  startTime));
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(currentLocationNew.getLatitude(), currentLocationNew.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
            if(addresses.size() == 0)
            {
                Toaster.showToast("Unable to get location data. Please try again with GPS switched on");
                return v;
            }

            //   String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            String city = addresses.get(0).getLocality();
            state = addresses.get(0).getAdminArea();
            String country = addresses.get(0).getCountryName();
            String postalCode = addresses.get(0).getPostalCode();
            String knownName = addresses.get(0).getFeatureName();
            //  Log.e("addressss", address);
            Log.e("city", city);
            Log.e("state", state);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
        Log.i(TAG, " 3 - " + (System.currentTimeMillis() -  startTime));

        spin_cities_filiter = (Spinner) v.findViewById(R.id.spin_cities_filiter);

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, Cities);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        // attaching data adapter to spinner
        spin_cities_filiter.setAdapter(dataAdapter);


        spin_cities_filiter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int postion, long l) {


                if (postion == 0) {

                    if (!bol_firsttime_zoom) {

                        zoom = 4;
                        bol_firsttime_zoom = true;


                    } else {
                        zoom = 10;
                        updateCities("Mumbai");
                    }
                } else if (postion == 1) {
                    zoom = 10;
                    updateCities("Bangalore");
                } else if (postion == 2) {
                    zoom = 10;
                    updateCities("Delhi");
                } else {
                    zoom = 4;
                    updateCities("Mumbai");
                }


            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        Log.i(TAG, " 4 - " + (System.currentTimeMillis() - startTime));
        v.findViewById(R.id.drawer_toggle).setOnClickListener(this);
        return v;
    }

    @Override
    public void onProceed(StepFragment<String> nextFragment) {
        if (nextFragment != null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.container, nextFragment)
                    .commit();
            FragmentManager fm = getChildFragmentManager();
            Fragment f = fm.findFragmentById(R.id.container);
            if (f != null) {
            } else {

                getChildFragmentManager().beginTransaction()
                        .remove(f)
                        .commit();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.hideToolbar(context);
        if (context instanceof MainActivity) {
            ((MainActivity) context).setBackPressListener(this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity activity = getActivity();
        Utils.showToolbar(activity);
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).setBackPressListener(null);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        Log.e("hereee on map ready", "here");
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_pin);
        Networker.getInstance().getCities(new Callback<List<CitiesResponse>>() {
            @Override
            public void success(List<CitiesResponse> citiesResponses, Response response) {
                {

                    Log.e("get responsee hereee", "" + citiesResponses.size());
                    for (int i = 0; i < citiesResponses.size(); i++) {
                        data = citiesResponses.get(i);
                        hash_map = new HashMap<String, String>();
                        hash_map.put("name", data.getName());
                        hash_map.put("location", data.getLocation());
                        hash_map.put("lat", data.getLat());
                        hash_map.put("long", data.getLong());
                        Al_cities.add(hash_map);

                        Log.e("dataa is heree ", "" + data.getName());
                    }
                    if (state.contains("Bangalore")) {
                        updateCities("Bangalore");
                        spin_cities_filiter.setSelection(1);
                    } else if (state.contains("Delhi")) {

                        updateCities("Delhi");
                        spin_cities_filiter.setSelection(2);
                    } else if (state.contains("Mumbai")) {

                        updateCities("Mumbai");
                        spin_cities_filiter.setSelection(0);
                    } else {
                        updateCities("Mumbai");
                        spin_cities_filiter.setSelection(0);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
        map.setOnMarkerClickListener(this);
    }

    public void updateCities(String cityname) {
        // temp solution for crash on multi tap on explore social button
        //// FIXME: 19/02/16 maps null return
        if(mapFragment.getMap() == null)
            return;

        Log.e("zomm is heree", "" + zoom);
        mapFragment.getMap().clear();
        for (int i = 0; i < Al_cities.size(); i++) {
            if (Al_cities.get(i).get("name").equalsIgnoreCase(cityname)) {
                //12.973703, 77.610372
                String lat = Al_cities.get(i).get("lat");
                String lon = Al_cities.get(i).get("long");
                BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.map_pin);
                LatLng Latlong = new LatLng(Double.parseDouble(lat), Double.parseDouble(lon));
                double Disatnce = distance(currentLocationNew.getLatitude(), currentLocationNew.getLongitude(), Double.parseDouble(lat), Double.parseDouble(lon));
                hash_distance = new HashMap<String, Double>();
                hash_distance.put("disatnce", Disatnce);
                hash_distance.put("lat", Double.parseDouble(lat));
                hash_distance.put("lon", Double.parseDouble(lon));
                Al_disatance.add(hash_distance);
                MarkerOptions options = new MarkerOptions()
                        .position(Latlong)
                        .icon(descriptor)
                        .title(Al_cities.get(i).get("location") + " " + new DecimalFormat("##.#").format(Disatnce) + "Km away");
                marker = mapFragment.getMap().addMarker(options);
                mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(Latlong, zoom));
            }
        }


        if (!bol_firsttime) {
            // measure  distance from Current Location to Outlets
            // Disaply Current location
            Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            Bitmap bmp = Bitmap.createBitmap(200, 50, conf);
            Canvas canvas = new Canvas(bmp);
            Paint paint = new Paint();
            paint.setTextSize(15);
            paint.setColor(Color.BLUE);
            canvas.drawText("You are here", 50, 40, paint); // paint defines the text color, stroke width, size
            mapFragment.getMap().addMarker(new MarkerOptions()
                            .position(new LatLng(currentLocationNew.getLatitude(),
                                    currentLocationNew.getLongitude()))
                                    //.icon(BitmapDescriptorFactory.fromResource(R.drawable.marker2))
                            .icon(BitmapDescriptorFactory.fromBitmap(bmp))
                            .anchor(0.5f, 1)
            );
            mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(currentLocationNew.getLatitude(), currentLocationNew.getLongitude()), zoom));
            bol_firsttime = true;
        }

        //   mapFragment.getMap().getUiSettings().setMyLocationButtonEnabled(true);
        mapFragment.getMap().setMyLocationEnabled(true);

        LatLng latlang = null;
        int i;
        int index = 0;
        double distance = Double.MAX_VALUE;

        for (i = 0; i < Al_disatance.size(); i++) {

            if (distance > Al_disatance.get(i).get("disatnce")) {
                distance = Al_disatance.get(i).get("disatnce");
                index = i;
            }
        }
        Log.e("hereee is data", "" + index);

        if (distance <= 15) {
            latlang = new LatLng(Al_disatance.get(index).get("lat"), Al_disatance.get(index).get("lon"));
            mapFragment.getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(latlang, 14.35f));
            if (tableNotBooked()) {

                Log.e("hereee table is booking", "yesss");
                showProgressDialog("Obtaining free tables");
                Networker.getInstance().getTables(new Callback<List<TablesResponse>>() {
                    @Override
                    public void success(List<TablesResponse> tablesResponses, Response response) {

                        {
                            mDialog.dismiss();
                            ArrayAdapter<TablesResponse> adapter = new FoundTablesAdapter(getActivity(), tablesResponses);
                            new AlertDialog.Builder(getActivity()).setTitle("Found free tables");
                            new AlertDialog.Builder(getActivity()).setAdapter(adapter, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    TablesResponse selected = adapter.getItem(which);
                                    showProgressDialog("Reserving");
                                    Networker.getInstance().openTable(selected.getTableCode(), 4,
                                            new Callback<Integer>() {
                                                @Override
                                                public void success(Integer integer, Response response) {
                                                    mDialog.dismiss();

                                                    //  mReservingStatus.setText(R.string.status_reserved);
                                                    //mReserveButton.setVisibility(View.GONE);
                                                    //mReservingStatus.setVisibility(View.VISIBLE);

                                                    Cart.instance().setPosOrderId(integer);
                                                    Cart.instance().setTableNo(selected.getTableId());
                                                    EventBus.getDefault().post(new ChangePageRequest(AppMenu.FOOD));
                                                }

                                                @Override
                                                public void failure(RetrofitError error) {
                                                    TablesFragment.this.failure(error);
                                                }
                                            });
                                }
                            });
                            new AlertDialog.Builder(getActivity()).setCancelable(false);
                            new AlertDialog.Builder(getActivity()).show();
                        }

                    }

                    @Override
                    public void failure(RetrofitError error) {


                    }
                });
            }
        }
    }


    public void failure(RetrofitError error) {
        Toast.makeText(getActivity(), "Error occurred", Toast.LENGTH_SHORT).show();
        Log.e(getClass().getSimpleName(), error.getMessage(), error);
        if (mDialog != null && mDialog.isShowing()) {
            mDialog.dismiss();
        }
    }


    private boolean tableNotBooked() {
        return Cart.instance().getTableNo(getActivity()) == -1;
    }

    private void showProgressDialog(String message) {
        mDialog = new ProgressDialog(getActivity());
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {


        String name = marker.getTitle();

        Log.e("get name", "" + name);
        if(name == null || name.length() == 0)
        return false ;

        Fragment fragment = new OutletScreenFragment();
        Bundle args = new Bundle();
        args.putString("name", name);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment.setArguments(args);
        fragmentTransaction.replace(R.id.container, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


      /*  FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment fragment = null;
        Bundle args = new Bundle();
        args.putString("name", name);
        fragment = new OutletScreenFragment();
        fragment.setArguments(args);

        if (fragment != null) {
            fm.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }*/
        /*FragmentManager fm = getChildFragmentManager();
        StepFragment<String> firstFragment = new LaterNowFragment();
        firstFragment.setPreviousResults(new ArrayList<String>());
        firstFragment.setProceedListener(this);
        fm.beginTransaction().replace(R.id.container, firstFragment).commit();*/


        return true;
    }

    @Override
    public void onClick(View v) {
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).openDrawer();
        }
    }

    @Override
    public boolean onBackPressed() {
        FragmentManager fm = getChildFragmentManager();

        Fragment fragment = fm.findFragmentById(R.id.container);
        if (fragment == null) {
            return false;
        } else {
            fm.beginTransaction().remove(fragment).commit();
            return true;
        }
    }


}
