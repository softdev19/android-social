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

import com.intrix.social.Data;
import com.intrix.social.DescriptionActivity;
import com.intrix.social.DiscoverSelfieActivity;
import com.intrix.social.MainActivity;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.ThingsLoveActivity;
import com.intrix.social.UniversalActivity;
import com.intrix.social.common.view.STextView;

/**
 * Created by toanpv on 11/30/15.
 */
public class DiscoverFragment extends Fragment implements View.OnClickListener {


    boolean data1Present = false;
    boolean data2Present = false;
    boolean data3Present = false;
    boolean data4Present = false;

    Data data;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_discover, container, false);

        data = MainApplication.data;
        STextView greetText = (STextView) v.findViewById(R.id.street_label);

        String name = "";
        String nameTemp = MainApplication.data.loadData("user.name");
        //greetText.setText("Hello there "+ name);

        String description2 = MainApplication.data.loadData("user.description2");
        String description3 = MainApplication.data.loadData("user.description3");
        if (description2.length() > 0 && description3.length() > 0)
            data1Present = true;
        String love1 = MainApplication.data.loadData("user.love1");
        String love2 = MainApplication.data.loadData("user.love2");
        String love3 = MainApplication.data.loadData("user.love3");

        if (love1.length() > 0 || love2.length() > 0 || love3.length() > 0)
            data2Present = true;

        String profilePic = data.getProfilePicUrl();
        if (profilePic.length() > 0)
            data3Present = true;

        for (int i = 0; i < 21; i++) {
            String talentLink = MainApplication.data.loadData("user.talentlink_" + i);
            if (talentLink.length() > 0) {
                data4Present = true;
                break;
            }
        }

        v.findViewById(R.id.btn_get_stated).setOnClickListener(this);
        v.findViewById(R.id.home).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_get_stated) {
            if (!data1Present) {
                startActivity(new Intent(getActivity(), DescriptionActivity.class));
            }else if (!data2Present) {
                startActivity(new Intent(getActivity(), ThingsLoveActivity.class));
            } else if (!data3Present)
                startActivity(new Intent(getActivity(), DiscoverSelfieActivity.class));
//            else if (!data4Present)
//                startActivity(new Intent(getActivity(), DiscoverTalentsActivity.class));
            else {
                Intent i = new Intent(getActivity(), UniversalActivity.class);
                i.putExtra(UniversalActivity.EXTRA_TOKEN, DiscoveringFragment.class);
                startActivity(i);
            }
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
}
