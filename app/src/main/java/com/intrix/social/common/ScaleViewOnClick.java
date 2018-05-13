package com.intrix.social.common;

import android.view.View;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class ScaleViewOnClick implements View.OnClickListener {

    private View[] mViews;
    private View.OnClickListener mListener;

    public ScaleViewOnClick(View.OnClickListener listener, View... views) {
        mViews = views;
        mListener = listener;
        for (View v : views) {
            v.setOnClickListener(this);
        }
    }

    public void setSelected(int position) {
        mViews[position].callOnClick();
    }

    @Override
    public void onClick(View clicked) {
        if (mListener != null) {
            mListener.onClick(clicked);
        }
        for (View v : mViews) {
            if (v != clicked) {
                v.animate().scaleX(1)
                        .scaleY(1).setDuration(200)
                        .start();
            } else {
                v.animate().scaleX(1.1f)
                        .scaleY(1.1f).setDuration(200)
                        .start();
            }
        }
    }
}
