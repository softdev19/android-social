package com.intrix.social.utils;

import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;


public class CurrentLocationNew extends Service implements LocationListener {

	private final Context _context;
	
	boolean isGPSEnabled = false;
	boolean isNetworkEnabled = false;
	boolean canGetLocation = false;
	Location location;
	double latitude;
	double longitude;
	float distence;
	String ACTION_FILTER = "com.example.proximityalert";
	private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10;
	private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;
	float dist;
	protected LocationManager locationManager;

	public CurrentLocationNew(Context context) {
		this._context = context;
		getLocation();
	}
	
	// =============================get current locationn updates==========//
	public Location getLocation() {
		try {
			locationManager = (LocationManager) _context
					.getSystemService(LOCATION_SERVICE);
			isGPSEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
			isNetworkEnabled = locationManager
					.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
			if (!isGPSEnabled && !isNetworkEnabled) {
				// no network provider is enabled
			} else {
				this.canGetLocation = true;
				if (isNetworkEnabled) {
					locationManager.requestLocationUpdates(
							LocationManager.NETWORK_PROVIDER,
							MIN_TIME_BW_UPDATES,
							MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
					Log.d("Network", "Network");
					if (locationManager != null) {
						location = locationManager
								.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
						if (location != null) {
							latitude = location.getLatitude();
							longitude = location.getLongitude();
						}
					}
				}
				// if GPS Enabled get lat/long using GPS Services
				if (isGPSEnabled) {
					if (location == null) {
						locationManager.requestLocationUpdates(
								LocationManager.GPS_PROVIDER,
								MIN_TIME_BW_UPDATES,
								MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
						Log.d("GPS Enabled", "GPS Enabled");
						if (locationManager != null) {
							location = locationManager
									.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								latitude = location.getLatitude();
								longitude = location.getLongitude();
							}
						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return location;
	}

	public void stopUsingGPS() {
		if (locationManager != null) {
			locationManager.removeUpdates(CurrentLocationNew.this);
		}
	}

	public double getLatitude() {
		if (location != null) {
			latitude = location.getLatitude();
		}
		return latitude;
	}

	public double getLongitude() {
		if (location != null) {
			longitude = location.getLongitude();
		}
		return longitude;
	}

	public float getDistanceTo(Location targetLocation) {
		if (location != null) {
			distence = location.distanceTo(targetLocation);
		}
		return distence;
	}

	public boolean canGetLocation() {
		return this.canGetLocation;
	}

	public void showSettingsAlert() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(_context);
		alertDialog.setTitle("Location settings");
		alertDialog
				.setMessage("Location detector is not enabled. Do you want to go to settings menu?");

		alertDialog.setPositiveButton("Settings",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent intent = new Intent(
								Settings.ACTION_LOCATION_SOURCE_SETTINGS);
						_context.startActivity(intent);
					}
				});

		alertDialog.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		alertDialog.show();
	}
	///////////////////////// =======================================//////////////////////
	
	
	///////////////////////// ==================on location change method=====================//////////////////////
	@Override
	public void onLocationChanged(Location location) {
		double latt = location.getLatitude();
		double longi = location.getLongitude();
		Log.e("Current Location", longi + "=============" + latt);


		Log.e("Map", "distance between Destination is=== " + dist);
		Toast.makeText(_context, "Distance between destination is " + dist,
				Toast.LENGTH_SHORT);
	}
	///////////////////////// =======================================//////////////////////
	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public static float distFrom(float lat1, float lng1, float lat2, float lng2) {
		double earthRadius = 6371000; // meters
		double dLat = Math.toRadians(lat2 - lat1);
		double dLng = Math.toRadians(lng2 - lng1);
		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
				+ Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLng / 2)
				* Math.sin(dLng / 2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
		float dist = (float) (earthRadius * c);

		return dist;
	}

	////////////////// REGISTERING PROXIMITY ALERT TO SHOW USER HAS REACHED BAR OR NOT////////////////////

	// =======================================//
	
	
	// =======================remove apromate reciver================//
	public void removeProximityAlert(Context context) {
		
		Intent intent = new Intent(ACTION_FILTER);
		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, -1,
				intent, Intent.FLAG_ACTIVITY_NEW_TASK);
	       locationManager.removeProximityAlert(pendingIntent);
		Log.d("REMOVE", "ProximityAlert removed");
		
	}
	
	// =======================================//

}
