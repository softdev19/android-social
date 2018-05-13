package com.intrix.social.common;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;

import com.intrix.social.MainActivity;
import com.intrix.social.networking.Networker;

import java.util.List;
import java.util.Locale;


/**
 * Created by sutharsha on 03/03/16.
 */



public class LocationAsync extends AsyncTask<Void, Void, Void> implements LocationListener {
    private Context ContextAsync;
    public LocationAsync (Context context){
        this.ContextAsync = context;
    }

    Dialog progress;
    private String providerAsync;
    private LocationManager locationManagerAsync;
    double   latAsync=0.0;
    double lonAsync=0.0;
    String thikanaAsync="Scan sms for location";

    String AddressAsync="";
    Geocoder GeocoderAsync;
    String obtainedCity = "";

    Location location;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progress = ProgressDialog.show(ContextAsync, "Loading data", "Please wait...");

    }


    @Override
    protected Void doInBackground(Void... arg0) {
        // TODO Auto-generated method stub
        locationManagerAsync = (LocationManager) ContextAsync.getSystemService(ContextAsync.LOCATION_SERVICE);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);
        criteria.setCostAllowed(false);
        criteria.setPowerRequirement(Criteria.NO_REQUIREMENT);
        providerAsync = locationManagerAsync.getBestProvider(criteria, false);

        if (locationManagerAsync.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            providerAsync = LocationManager.GPS_PROVIDER;
        } else if (locationManagerAsync.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            providerAsync = LocationManager.NETWORK_PROVIDER;
            AlertDialog.Builder alert = new AlertDialog.Builder(ContextAsync);
            alert.setTitle("GPS is disabled in the settings!");
            alert.setMessage("It is recomended that you turn on your device's GPS and restart the app so the app can determine your location more accurately!");
            alert.setPositiveButton("OK", null);
            alert.show();
        } else if (locationManagerAsync.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)) {
            providerAsync = LocationManager.PASSIVE_PROVIDER;
            //Toast.makeText(ContextAsync, "Please turn on data", Toast.LENGTH_LONG).show();
            //Toaster.toast("Please turn on data", true);
        }

        location = locationManagerAsync.getLastKnownLocation(providerAsync);
        // Initialize the location fields
        if (location != null) {
            //  System.out.println("Provider " + provider + " has been selected.");
            latAsync = location.getLatitude();
            lonAsync = location.getLongitude();

        } else {
            //Toast.makeText(ContextAsync, " Locationnot available", Toast.LENGTH_SHORT).show();
        }

        List<Address> addresses = null;
        GeocoderAsync = new Geocoder(ContextAsync, Locale.getDefault());
        try {
            addresses = GeocoderAsync.getFromLocation(latAsync, lonAsync, 1);

            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getAddressLine(1);
            //obtainedCity = city;
            String country = addresses.get(0).getCountryName();
            AddressAsync = Html.fromHtml(
                    address + ", " + city + ",<br>" + country).toString();

            if (addresses.size() > 0) {
                System.out.println(addresses.get(0).getLocality());
                obtainedCity = addresses.get(0).getLocality();
            }
        } catch (Exception e) {
            e.printStackTrace();
            AddressAsync = "Refresh for the address";
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        onLocationChanged(location);
        progress.dismiss();
        Log.v("latAsync_lonAsync", latAsync + "_" + lonAsync);
        Intent intentAsync = new Intent(ContextAsync,MainActivity.class);
        intentAsync.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intentAsync.putExtra("calculated_Lat", latAsync);
        intentAsync.putExtra("calculated_Lon", lonAsync);
        intentAsync.putExtra("calculated_address", AddressAsync);
        intentAsync.putExtra("city", obtainedCity);

        if(latAsync > 0 && lonAsync > 0){// &&  obtainedCity.length() > 0) {
            Networker.getInstance().setUserLocation(latAsync, lonAsync);
        }
        ContextAsync.startActivity(intentAsync);
    }



    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        locationManagerAsync.requestLocationUpdates(providerAsync, 0, 0, this);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }
}
