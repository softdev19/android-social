package com.intrix.social.common;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.intrix.social.R;

public class UsefullData implements ConstantValue {

	private Context _context;
	private ProgressDialog pDialog;

	public UsefullData(Context c) {
		_context = c;
	}

	// ================== DEVICE INFORMATION ============//

	public static String getCountryCodeFromDevice() {
		String countryCode = Locale.getDefault().getCountry();
		if (countryCode.equals("")) {
			countryCode = "IN";
		}
		return countryCode;
	}

	public String getDeviceId() {

		String deviceId = "";

		TelephonyManager telephonyManager = (TelephonyManager) _context
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
			deviceId = telephonyManager.getDeviceId();
		} else {
			deviceId = Secure.getString(_context.getContentResolver(),
					Secure.ANDROID_ID);
		}
		Log("Your Device Id :" + deviceId);
		return deviceId;
	}

	// ================== GET TIME AND DATE ============//

	@SuppressLint("SimpleDateFormat")
	public static String getDateTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm aa");
		Calendar cal = Calendar.getInstance();
		// sdf.applyPattern("dd MMM yyyy");
		String strDate = sdf.format(cal.getTime());
		return strDate;
	}
	
	
	public static String getDateTime(String date) {
		
		/*//2014-11-06 13:19:32
		SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy HH:mm aa");
		Calendar cal = Calendar.getInstance();
		// sdf.applyPattern("dd MMM yyyy");
		String strDate = sdf.format(new Date(date));
		return strDate;*/
		
		//String date = "2011/11/12 16:05:06";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-mm-dd HH:MM:SS");
        Date testDate = null;
        try {
            testDate = sdf.parse(date);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        SimpleDateFormat formatter = new SimpleDateFormat("dd MMM yyyy HH:mm aa");
        String newFormat = formatter.format(testDate);
       // System.out.println(".....Date..."+newFormat);
        return newFormat;
		
	}
	

	@SuppressLint("SimpleDateFormat")
	public static String getTime() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH:mm aa");
		Calendar cal = Calendar.getInstance();
		// sdf.applyPattern("dd MMM yyyy");
		String strTime = sdf.format(cal.getTime());
		return strTime;

	}

	// ================== CREATE FILE AND RELATED ACTION ============//

	public File getRootFile() {

		File f = new File(Environment.getExternalStorageDirectory(), _context
				.getString(R.string.app_name).toString());
		if (!f.isDirectory()) {
			f.mkdirs();
		}

		return f;
	}

	public void deleteRootDir(File root) {

		if (root.isDirectory()) {
			String[] children = root.list();
			for (int i = 0; i < children.length; i++) {
				File f = new File(root, children[i]);
				Log("file name:" + f.getName());
				if (f.isDirectory()) {
					deleteRootDir(f);
				} else {
					f.delete();
				}
			}
		}
	}

	public File createFile(String fileName) {
		File f = null;
		try {
			f = new File(getRootFile(), fileName);
			if (f.exists()) {
				f.delete();
			}

			f.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return f;
	}

	// ================ DOWNLOAD =================//

	public void downloadAndDisplayImage(final String image_url,
			final ImageView v) {

		new Thread() {

			@Override
			public void run() {
				try {

					InputStream in = new URL(image_url).openConnection()
							.getInputStream();
					Bitmap bm = BitmapFactory.decodeStream(in);
					File fileUri = new File(getRootFile(),
							getNameFromURL(image_url));
					FileOutputStream outStream = null;
					outStream = new FileOutputStream(fileUri);
					bm.compress(Bitmap.CompressFormat.JPEG, 75, outStream);
					outStream.flush();
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {

					File f = new File(getRootFile(), "aa.jpg");
					if (f.exists()) {
						final Bitmap bmp = BitmapFactory
								.decodeFile(f.getPath());

						v.post(new Runnable() {

							@Override
							public void run() {
								// TODO Auto-generated method stub
								v.setImageBitmap(bmp);
							}
						});

						Log("download images and showing ,,,,");

					}
				}
			}

		}.start();
	}

	public String getNameFromURL(String url) {

		String fileName = "item_image.jpg";
		if (url != null) {
			fileName = url.substring(url.lastIndexOf('/') + 1, url.length());
		}
		return fileName;
	}

	// ===================================//
	public String getFileName(String url) {

		/*
		 * String str = String.valueOf(url.charAt(0)) +
		 * url.substring(url.length() / 2,url.length() / 2+2) +
		 * String.valueOf(url.charAt(url.length() - 1)) + "_" + url.length() +
		 * ".jpg";
		 */
		String str = url.substring(url.lastIndexOf('/') + 1, url.length());

		if (!str.endsWith(".jpg")) {
			str = str.replaceAll("[^\\w\\s\\-_]", "") + ".jpg";
		}

		UsefullData.Log("File name: " + str);
		return str;
	}

	// ================== LOG AND TOAST ====================//

	public static void Log(final String msg) {

		if (SHOW_LOG) {
			android.util.Log.e(LOG_TAG, msg);
		}

	}

	public void showMsgOnUI(final String msg) {
		((Activity) _context).runOnUiThread(new Runnable() {

			@Override
			public void run() {
				Toast.makeText(_context, msg, Toast.LENGTH_SHORT).show();
			}
		});

	}

	// =================== INTERNET ===================//
	public boolean isNetworkConnected() {
		ConnectivityManager cm = (ConnectivityManager) _context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getActiveNetworkInfo();
		if (ni == null) {
			return false;
		} else
			return true;
	}

	// ==================== PROGRESS DIALOG ==================//

	public void showProgress(final String msg, final String title) {
		pDialog = ProgressDialog.show(_context, "", msg, true);
		

	}

	public void dismissProgress() {
		 try {
		if (pDialog != null) {
			
			if (pDialog.isShowing()) {
				pDialog.dismiss();
				pDialog = null;
			}
		}
		 } catch (final IllegalArgumentException e) {
		        // Handle or log or ignore
		    } catch (final Exception e) {
		        // Handle or log or ignore
		    } finally {
		        this.pDialog = null;
		    }  
	}

	// ==================== HIDE KEYBOARED ==================//
	public void hideKeyBoared() {

		InputMethodManager imm = (InputMethodManager) _context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

	}

	// ====================SET FONT SIZE==================//
	public Typeface getUsedFontLucida() {
		Typeface typeFace = Typeface.createFromAsset(_context.getAssets(),
				"fonts/lucida_hand_writting.ttf");
		return typeFace;
	}

	public Typeface getUsedFontArial() {
		Typeface typeFace = Typeface.createFromAsset(_context.getAssets(),
				"fonts/arial.ttf");
		return typeFace;
	}

	public Typeface getPunjabiFont() {
		Typeface typeFace = Typeface.createFromAsset(_context.getAssets(),
				"fonts/DroidSansFallback.ttf");
		return typeFace;
	}

}
