package com.intrix.social.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.MainActivity;
import com.intrix.social.PeopleActivity;
import com.intrix.social.R;

/**
 * Created by toanpv on 11/30/15.
 */
public class PeopleResultFragment extends Fragment implements View.OnClickListener {


    boolean data1Present = false;
    boolean data2Present = false;
    boolean data3Present = false;
    boolean data4Present = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_people_result, container, false);



//        v.findViewById(R.id.btn_get_stated).setOnClickListener(this);
//        v.findViewById(R.id.home).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_stated) {

        } else if (v.getId() == R.id.home) {
            Activity activity = getActivity();
            if (activity instanceof MainActivity) {
                ((MainActivity) activity).openDrawer();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof AppCompatActivity) {
            ActionBar ab = ((AppCompatActivity) context).getSupportActionBar();
            if (ab != null) {
                ab.hide();
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Activity activity = getActivity();
        if (activity instanceof AppCompatActivity) {
            ActionBar ab = ((AppCompatActivity) activity).getSupportActionBar();
            if (ab != null && !ab.isShowing()) {
                ab.show();
            }
        }
    }


    public void onClickPeople(View v) {
        startActivity(new Intent(getActivity(), PeopleActivity.class));
    }
}
