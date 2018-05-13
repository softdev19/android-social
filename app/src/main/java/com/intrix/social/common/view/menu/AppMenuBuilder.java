package com.intrix.social.common.view.menu;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Arrays;

/**
 * Created by yarolegovich on 01.01.2016.
 */
public class AppMenuBuilder {

    public static AppMenuBuilder withContext(Context context) {
        return new AppMenuBuilder(context);
    }

    private Context mContext;
    private AppMenu.ItemSelectedListener mListener;
    private int mBg = -1;
    private int mBgTint = -1;
    private AppMenuItem[] mItems;
    private View mHeader;
    private int mGradient = 1;

    public AppMenuBuilder(Context context) {
        mContext = context;
    }

    public AppMenuBuilder addItems(AppMenuItem...items) {
        mItems = items;
        return this;
    }

    public AppMenuBuilder withListener(AppMenu.ItemSelectedListener listener) {
        mListener = listener;
        return this;
    }

    public AppMenuBuilder withBgTint(int color) {
        mBgTint = color;
        return this;
    }

    public AppMenuBuilder withBg(int bgRes) {
        mBg = bgRes;
        return this;
    }

    public AppMenuBuilder withHeader(@LayoutRes int layout) {
        mHeader = LayoutInflater.from(mContext).inflate(layout, null);
        return this;
    }

    public AppMenuBuilder withHeader(View layout) {
        mHeader = layout;
        return this;
    }

    public AppMenuBuilder withGradient(int resource) {
        mGradient = resource;
        return this;
    }

    public AppMenu bindTo(View v) {
        if (!(v instanceof ViewGroup)) {
            throw new RuntimeException("Can only add to a viewgroup");
        }
        ViewGroup container = (ViewGroup) v;
        AppMenu menu = new AppMenu(mContext);
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        menu.setLayoutParams(params);
        if (mBg != -1) {
            menu.setBgImage(mBg);
        }
        if (mBgTint != -1) {
            menu.setBgTint(mBgTint);
        }
        if (mHeader != null) {
            menu.addHeader(mHeader);
        }
        if (mGradient != -1) {
            menu.setGradientOverlay(mGradient);
        }
        menu.setItemSelectedListener(mListener);
        menu.addItems(Arrays.asList(mItems));
        container.addView(menu);
        return menu;
    }
}
