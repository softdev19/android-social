/* Copyright (c) 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.intrix.social.utils;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.TabLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.mikepenz.fontawesome_typeface_library.FontAwesome;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

/**
 * Class containing some static utility methods.
 */
public class Utils {

    private static Typeface sMontana;

    private Utils() {
    }

    public static Typeface montanaFont(Context context) {
        return sMontana != null ? sMontana :
                (sMontana = Typeface.createFromAsset(context.getAssets(), "fonts/montana-regular.otf"));
    }

    public static boolean hasFroyo() {
        // Can use static final constants like FROYO, declared in later versions
        // of the OS since they are inlined at compile time. This is guaranteed behavior.
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
    }

    public static boolean hasGingerbread() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
    }

    public static boolean hasHoneycomb() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
    }

    public static boolean hasHoneycombMR1() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
    }

    public static boolean hasJellyBean() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
    }

    /**
     * Logs the given throwable and shows an error alert dialog with its message.
     *
     * @param activity activity
     * @param tag      log tag to use
     * @param t        throwable to log and show
     */
    /** public static void logAndShow(Activity activity, String tag, Throwable t) {
     Log.e(tag, "Error", t);
     String message = t.getMessage();
     if (t instanceof GoogleJsonResponseException) {
     GoogleJsonError details = ((GoogleJsonResponseException) t).getDetails();
     if (details != null) {
     message = details.getMessage();
     }
     } else if (t.getCause() instanceof GoogleAuthException) {
     message = ((GoogleAuthException) t.getCause()).getMessage();
     }
     showError(activity, message);
     }
     **/

    /**
     * Logs the given message and shows an error alert dialog with it.
     *
     * @param activity activity
     * @param tag      log tag to use
     * @param message  message to log and show or {@code null} for none
     */
    public static void logAndShowError(Activity activity, String tag, String message) {
        String errorMessage = getErrorMessage(activity, message);
        Log.e(tag, errorMessage);
        showErrorInternal(activity, errorMessage);
    }

    /**
     * Shows an error alert dialog with the given message.
     *
     * @param activity activity
     * @param message  message to show or {@code null} for none
     */
    public static void showError(Activity activity, String message) {
        String errorMessage = getErrorMessage(activity, message);
        showErrorInternal(activity, errorMessage);
    }

    private static void showErrorInternal(final Activity activity, final String errorMessage) {
        activity.runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(activity, errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private static String getErrorMessage(Activity activity, String message) {
        Resources resources = activity.getResources();
        if (message == null) {
            return resources.getString(R.string.error);
        }
        return resources.getString(R.string.error_format, message);
    }

    //ffmpeg
    public static long getProcessTime(String message) {
        //frame=   83 fps=6.4 q=25.0 size=    5568kB time=00:00:03.05 bitrate=14947.2kbits/s
        int timeIndex = message.indexOf("time=");
        int bitrateIndex = message.indexOf(" bitrate=");

        if (timeIndex >= 0 && bitrateIndex >= 0) {
            String timeStr = message.substring(timeIndex + 5, bitrateIndex);

            return toMillis(timeStr);
        }

        return -1;
    }

    public static long toMillis(String time) {
        // 00:00:11.76
        String[] split = time.split(":");

        int hours = Integer.valueOf(split[0]);
        int minutes = Integer.valueOf(split[1]);
        double seconds = Double.valueOf(split[2]);

        return (long) (1000 * (60 * 60 * hours + 60 * minutes + seconds));
    }

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

    public static void applyFontedTab(Context context, TabLayout tabLayout) {
        ViewGroup vg = (ViewGroup) tabLayout.getChildAt(0);
        int tabsCount = vg.getChildCount();
        Typeface tf = FontCache.get("fonts/montana-regular.otf", context);
        for (int j = 0; j < tabsCount; j++) {
            ViewGroup vgTab = (ViewGroup) vg.getChildAt(j);
            int tabChildsCount = vgTab.getChildCount();
            for (int i = 0; i < tabChildsCount; i++) {
                View tabViewChild = vgTab.getChildAt(i);
                if (tabViewChild instanceof TextView) {
                    ((TextView) tabViewChild).setTypeface(tf, Typeface.NORMAL);
                }
            }
        }
    }

    public static Bitmap scaleBitmap(Bitmap bm, float scalingFactor) {
        int scaleHeight = (int) (bm.getHeight() * scalingFactor);
        int scaleWidth = (int) (bm.getWidth() * scalingFactor);

        return Bitmap.createScaledBitmap(bm, scaleWidth, scaleHeight, true);
    }

    public static PrimaryDrawerItem primaryItem(Context context, int name, FontAwesome.Icon icon, String badge) {
        PrimaryDrawerItem item = new PrimaryDrawerItem()
                .withName(context.getString(name))
                .withBadge(badge)
                .withTextColorRes(R.color.menu_text)
                .withSelectedTextColorRes(R.color.menu_text_selected)
                .withIconColorRes(R.color.menu_icon)
                .withSelectedIconColorRes(R.color.menu_icon_selected)
                .withTypeface(Utils.montanaFont(context));
        if (icon != null) {
            item.withIcon(icon);
        } else item.withIcon(0);
        return item;
    }

    public static List<String> replicate(String s, int times) {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < times; i++) {
            result.add(s);
        }
        return result;
    }

    public static String[] getArrivalTimes(Context context) {
        Calendar calendar = Calendar.getInstance();
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.FRIDAY || dayOfWeek == Calendar.SATURDAY) {
            return context.getResources().getStringArray(R.array.weekendTimes);
        } else {
            return context.getResources().getStringArray(R.array.weekdayTimes);
        }
    }

    public static File uriToFile(Uri uri) {
        ContentResolver cr = MainApplication.instance.getContentResolver();
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cur = cr.query(uri, projection, null, null, null);
        assert cur != null;
        if (!cur.moveToFirst()) return null;
        String filePath = cur.getString(0);
        return new File(filePath);
    }

    public static Map<String, String> getClourdinaryConfig() {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", "intrix");
        config.put("api_key", "229163992359269");
        config.put("api_secret", "NY7c85xpqvohYaT0p9iCLy5O18k");
        return config;
    }

public static void hideToolbar(Context context) {
        ActionBar ab = getActionBar(context);
        if (ab != null) {
            ab.hide();
        }
    }

    public static void showToolbar(Context context) {
        ActionBar ab = getActionBar(context);
        if (ab != null && !ab.isShowing()) {
            ab.show();
        }
    }

    public static ActionBar getActionBar(Context context) {
        if (context instanceof AppCompatActivity) {
            return ((AppCompatActivity) context).getSupportActionBar();
        }
        return null;
    }

    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    static TimeZone tz = TimeZone.getTimeZone("Asia/Calcutta");
    public static Date dateConverter(String ds)
    {

        if(tz == null)
             tz = TimeZone.getTimeZone("Asia/Calcutta");
        //Calendar cal = Calendar.getInstance(tz);

        if(sdf == null)
             sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        sdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date parsed = null; // => Date is in UTC now
        try {
            parsed = sdf.parse(ds);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        destFormat.setTimeZone(tz);

        Date date;
        String dateString;
        dateString = destFormat.format(parsed);

        try {
            date = destFormat.parse(dateString);
        } catch (ParseException e) {
            date = new Date();
            e.printStackTrace();
        }
        return date;


//        sdf.setCalendar(cal);
//        Date date;
//        try {
//            cal.setTime(sdf.parse(ds));
//
//            date = cal.getTime();
//        } catch (ParseException e) {
//            date = new Date();
//            e.printStackTrace();
//        }

//        return date;
    }

    public static int dpToPx(float dp)
    {
        return (int) (dp * Resources.getSystem().getDisplayMetrics().density);
    }

    public static float pxToDp(int px)
    {
        return (float) (px / Resources.getSystem().getDisplayMetrics().density);
    }
}
