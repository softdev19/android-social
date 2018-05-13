package com.intrix.social.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.R;

/**
 * Created by yarolegovich on 24.12.2015.
 */
public class ComingSoonFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        AppCompatActivity a = (AppCompatActivity) getActivity();
        ActionBar ab = a.getSupportActionBar();
        assert ab != null;
        ab.setBackgroundDrawable(new ColorDrawable(ContextCompat.getColor(a, R.color.splash_accent)));
        return inflater.inflate(R.layout.fragment_coming_soon, container, false);
    }
}
