package com.intrix.social.fragment;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.intrix.social.R;
import com.intrix.social.model.event.SwitchFragEvent;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.listener.multi.DialogOnAnyDeniedMultiplePermissionsListener;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class SignInFragment extends Fragment {

    //View signUpButton;  Changed By Damoc
    View googleButton;
    View faceBookButton;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sign_in, container, false);

        /* Changed by damoc
        signUpButton = v.findViewById(R.id.sign_up);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SwitchFragEvent("SignUp Manual"));
            }
        });*/

//        MultiplePermissionsListener dialogMultiplePermissionsListener =
//                DialogOnAnyDeniedMultiplePermissionsListener.Builder
//                        .withContext(getContext())
//                        .withTitle("Account access permission")
//                        .withMessage("We require your permission to use data like email, name etc.")
//                        .withButtonText(android.R.string.ok)
//                        .build();
//        Dexter.checkPermissions(dialogMultiplePermissionsListener,
//                Manifest.permission.GET_ACCOUNTS);

        /* Changed by damoc
        googleButton = v.findViewById(R.id.sign_in_google);
        googleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestPermission();
                EventBus.getDefault().post(new SwitchFragEvent("SignIn Google"));
            }
        });*/

        faceBookButton = v.findViewById(R.id.sign_in_facebook);
        faceBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //requestPermission();
                EventBus.getDefault().post(new SwitchFragEvent("SignIn Facebook"));
            }
        });
        return v;
    }

    private void requestPermission() {
        MultiplePermissionsListener dialogMultiplePermissionsListener =
                DialogOnAnyDeniedMultiplePermissionsListener.Builder
                        .withContext(getContext())
                        .withTitle("Account access permission")
                        .withMessage("We require your permission to use data like email, name etc.")
                        .withButtonText(android.R.string.ok)
                        .build();
        Dexter.checkPermissions(dialogMultiplePermissionsListener,
                Manifest.permission.GET_ACCOUNTS);
    }

}
