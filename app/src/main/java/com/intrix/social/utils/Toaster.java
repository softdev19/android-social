package com.intrix.social.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by sutharsha on 28/04/15.
 */
public class Toaster {

    static Context cont;

    public static void setContext(Context context)
    {
        cont = context;
    }

    public static void toast(String str)
    {
        toast(str, false);
    }

    public static void toast(String str, boolean appToast)
    {
        if(appToast)
            showToast(str);
    }

    public static void showToast(String str)
    {
        Toast t = new Toast(cont);
        TextView txtView = new TextView(cont);
        txtView.setText(str);
        txtView.setTextSize(16);
        txtView.setBackgroundColor(Color.GRAY);
        txtView.setPadding(20,20,20,20);
        txtView.setTextColor(Color.WHITE);
        txtView.setGravity(Gravity.CENTER);
        t.setView(txtView);
        t.setDuration(Toast.LENGTH_SHORT);
        t.show();
    }


    public static void toast(Context cont, String str)
    {
        Toast t = new Toast(cont);
        TextView txtView = new TextView(cont);
        txtView.setText(str);
        txtView.setTextSize(16);
        txtView.setBackgroundColor(Color.GRAY);
        txtView.setPadding(20,20,20,20);
        txtView.setTextColor(Color.WHITE);
        txtView.setGravity(Gravity.CENTER);
        t.setView(txtView);
        t.setDuration(Toast.LENGTH_SHORT);
        t.show();
    }

    public static void toastLong(Context cont, String str)
    {
        Toast t = new Toast(cont);
        TextView txtView = new TextView(cont);
        txtView.setText(str);
        txtView.setGravity(Gravity.CENTER);
        t.setView(txtView);
        t.setDuration(Toast.LENGTH_LONG);
        t.show();
    }
}
