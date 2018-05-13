package com.intrix.social.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.MainActivity;
import com.intrix.social.R;
import com.intrix.social.common.AppMenu;
import com.intrix.social.utils.Utils;

/**
 * Created by yarolegovich on 28.01.2016.
 */
public class ConfirmedFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_confirmed, container, false);

        v.findViewById(R.id.btn_food).setOnClickListener(this);
        v.findViewById(R.id.btn_events).setOnClickListener(this);
        v.findViewById(R.id.btn_startups).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        int startFragment = 0;
        boolean openFab = false;
        switch (v.getId()) {
            case R.id.btn_food:
                startFragment = AppMenu.FOOD;
                openFab = true;
                break;
            case R.id.btn_events:
                startFragment = AppMenu.EVENTS;
                break;
            case R.id.btn_startups:
                startFragment = AppMenu.WORKSPACE;
                break;
        }
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra(MainActivity.ARG_START_FRAGMENT, startFragment);
        intent.putExtra(MainActivity.ARG_OPEN_FAB, openFab);
        startActivity(intent);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.hideToolbar(context);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Utils.showToolbar(getActivity());
    }
}
