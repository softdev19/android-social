/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intrix.social.chat.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.provider.Telephony;
import android.provider.Telephony.Sms.Intents;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListAdapter;
import android.widget.ListView;

import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Utils {

    final private static String TAG = "Utils";

    /**
     * Check if the device runs Android 4.3 (JB MR2) or higher.
     */
    public static boolean hasJellyBeanMR2() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN_MR2;
    }

    /**
     * Check if the device runs Android 4.1 (JB) or higher.
     */
    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Check if the device runs Android 4.4 (KitKat) or higher.
     */
    public static boolean hasKitKat() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.KITKAT;
    }

    /**
     * Check if the device runs Android 5.0 (Lollipop) or higher.
     */
    public static boolean hasLollipop() {
        return Build.VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {

            byte[] bytes = new byte[buffer_size];
            for (; ; ) {
                //Read byte from input stream

                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;

                //Write byte from output stream
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static void hideSoftwareKeyboard(Activity context) {
        View inFocus = context.getCurrentFocus();
        if (inFocus != null) {
            InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(inFocus.getWindowToken(), 0);
        }
    }

    /**
     * Check if your app is the default system SMS app.
     *
     * @param context The Context
     * @return True if it is default, False otherwise. Pre-KitKat will always return True.
     */
    public static boolean isDefaultSmsApp(Context context) {
        if (hasKitKat()) {
            return context.getPackageName().equals(Telephony.Sms.getDefaultSmsPackage(context));
        }

        return true;
    }

    /**
     * Trigger the intent to open the system dialog that asks the user to change the default
     * SMS app.
     *
     * @param context The Context
     */
    public static void setDefaultSmsApp(Context context) {
        // This is a new intent which only exists on KitKat
        if (hasKitKat()) {
            Intent intent = new Intent(Intents.ACTION_CHANGE_DEFAULT);
            intent.putExtra(Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
            context.startActivity(intent);
        }
    }

    public static void updateParse() {
//        ParseInstallation parseInstallation = ParseInstallation.getCurrentInstallation();
//        parseInstallation.put("email", MainApplication.data.loadData("user.email"));
//        parseInstallation.put("mobile", MainApplication.data.getMobile());
//        parseInstallation.saveInBackground();
    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        return resizedBitmap;
    }

    public static Bitmap[] splitBitmapAtHeight(Bitmap picture, int height) {
        Bitmap[] imgs = new Bitmap[2];
        imgs[0] = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), height);
        imgs[1] = Bitmap.createBitmap(picture, 0, height, picture.getWidth(), picture.getHeight() - height);
        return imgs;
    }

    public static Bitmap[] splitBitmapAtHeight(Bitmap picture, int heightfull, int height) {

        Log.i(TAG, "Cur height-" + height + " - " + picture.getHeight() + " - " + heightfull);
        height = height *  picture.getHeight()/heightfull;
        Log.i(TAG, "New height-" + height + " - " + picture.getHeight() + " - " + heightfull);

        Bitmap[] imgs = new Bitmap[2];
        imgs[0] = Bitmap.createBitmap(picture, 0, 0, picture.getWidth(), height);
        imgs[1] = Bitmap.createBitmap(picture, 0, height, picture.getWidth(), picture.getHeight() - height);
        return imgs;
    }

    public static void setTotalHeightofListView(ListView listView) {

        int totalHeight = 0;
        ListAdapter mAdapter = listView.getAdapter();
        for (int i = 0; i < mAdapter.getCount(); i++) {
            View mView = mAdapter.getView(i, null, listView);
            mView.measure(
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

            totalHeight += mView.getMeasuredHeight();
            Log.w("HEIGHT" + i, String.valueOf(totalHeight));
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (mAdapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static Date getDate(String dateString) {
        if(dateString.length() == 0) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(1989,1,22);
            return calendar.getTime(); // Ujjwal's birthday
        }
        //Log.e(TAG,"="+dateString+"-+-");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date convertedDate = new Date();
        try{
            convertedDate = dateFormat.parse(dateString);
        } catch (ParseException e){
            Log.e(TAG, dateString);
            e.printStackTrace();
        }
        return convertedDate;
    }

    public static boolean isDecimal(float number)
    {
        return (number % 1 != 0);
    }


    static DecimalFormat dF;
    static DecimalFormat dFnd; //no decimal
    public static String shredDecimal(float input)
    {
        if(dF == null)
            dF = new DecimalFormat("##,##,###.0");

        if(dFnd == null)
            dFnd = new DecimalFormat("##,##,###");


        if(isDecimal(input))
            return ""+dF.format(input);
        else
            return ""+dFnd.format((int)input);

    }

    public static String currencySymbol = "₹";
    public static String croreSymbol = "Cr";
    public static String formatAmount(float amount)
    {
        if(amount < 0.1 )
            return "N/A";

        String processedAmount = "";

        if(amount > 9999999) {
            amount = (amount / 10000000);
            processedAmount = currencySymbol +" "+ shredDecimal(amount) +" "+ croreSymbol;
        }else
            processedAmount = currencySymbol +" "+ shredDecimal(amount);

        return processedAmount;
    }

    public static int getDayDiff(Date startDate, Date endDate) {

        long diff = endDate.getTime() - startDate.getTime();
        float dayCount = (float) diff / (24 * 60 * 60 * 1000);
        return (int)dayCount;
    }

    long unixtime;
    public long timeConversion(String time)
    {
        DateFormat dfm = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        //dfm.setTimeZone(TimeZone.getTimeZone("GMT+5:30"));//Specify your timezone
        try
        {
            unixtime = dfm.parse(time).getTime();
            unixtime=unixtime/1000;
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return unixtime;
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window w = activity.getWindow();
            w.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            w.setStatusBarColor(color);
        }
    }
}
