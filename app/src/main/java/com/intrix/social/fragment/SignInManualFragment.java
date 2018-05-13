package com.intrix.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.intrix.social.MainActivity;
import com.intrix.social.R;
import com.intrix.social.model.event.MessageEvent;
import com.intrix.social.model.event.SwitchFragEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.Toaster;
import com.intrix.social.utils.Utils;

import de.greenrobot.event.EventBus;

/**
 * Created by yarolegovich on 22.11.2015.
 */
public class SignInManualFragment extends Fragment {

    private static final String TAG ="SignInManualFragment";

    EditText emailView;
    EditText phoneNumberView;
    EditText passwordView;
    View signUpButton;
    View signInButton;
    private Networker mNetworker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        View v = inflater.inflate(R.layout.fragment_sign_in_manual, container, false);

        emailView = (EditText) v.findViewById(R.id.email_id);
        passwordView = (EditText) v.findViewById(R.id.password);
        mNetworker = Networker.getInstance();
        signInButton = v.findViewById(R.id.sign_in_btn);
        signUpButton = v.findViewById(R.id.new_account);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Data data = MainApplication.data;
                //data.getCategories();

                final String email = emailView.getText().toString();
                final String password = passwordView.getText().toString();
//              final String phoneNumber = phoneNumberView.getText().toString();
                if (email.length() == 0) {
                    Toaster.toast(
                            "Please enter Email", true);
                    return;
                } else if (!Utils.isValidEmail(email)) {
                    Toaster.toast(
                            "Invalid Email", true);
                    return;
                }

                if (password.length() == 0) {
                    Toaster.toast(
                            "Please enter password", true);
                    return;
                }
// else if (password.length() < 6) {
//                    Toaster.toast(
//                            "Invalid Password - Password need to be atleast 6 characters long", true);
//                    return;
//                }

                Log.i(TAG, "SignIn before call");
                mNetworker.signIn(email, password);

                //MainApplication mainApp = (MainApplication)getActivity().getApplication();
//                Data data = MainApplication.data;
//                data.getCategories();

            }
        });

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SwitchFragEvent("SignUp Manual"));
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    // This method will be called when a MessageEvent is posted
    public void onEvent(MessageEvent event) {
        if (event.message.contains("SignIn success")) {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
        Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
    }
}
