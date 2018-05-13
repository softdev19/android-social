package com.intrix.social.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
public class SignUpFragment extends Fragment {

    EditText emailView;
    EditText phoneNumberView;
    EditText passwordView;
    EditText confirmPasswordView;
    View signUpButton;
    View signInButton;
    private Networker mNetworker;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        EventBus.getDefault().register(this);

        View v = inflater.inflate(R.layout.fragment_sign_up2, container, false);

        emailView = (EditText)v.findViewById(R.id.email_id);
        phoneNumberView = (EditText)v.findViewById(R.id.mobile_number);
        passwordView = (EditText)v.findViewById(R.id.password);
        confirmPasswordView = (EditText)v.findViewById(R.id.confirm_password);
        mNetworker = Networker.getInstance();
        signUpButton = v.findViewById(R.id.sign_up_btn);
        signInButton = v.findViewById(R.id.go_to_sign_in);

        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = emailView.getText().toString();
                final String password = passwordView.getText().toString();
                final String confirmPassword = confirmPasswordView.getText().toString();
                final String phoneNumber = phoneNumberView.getText().toString();
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
                } else if (password.length() < 6) {
                    Toaster.toast(
                            "Invalid Password - Password need to be atleast 6 characters long", true);
                    return;
                } else if (password.compareTo(confirmPassword) != 0) {
                    Toaster.toast(
                            "Passoword confirmation does not match", true);
                }

                if (phoneNumber.length() == 0) {
                    Toaster.toast(
                            "Please enter mobile number", true);
                    return;
                } else if (phoneNumber.length() < 10) {
                    Toaster.toast(
                            "Please enter valid mobile number", true);
                    return;
                }

                mNetworker.signUp(email, password, confirmPassword);

                //MainApplication mainApp = (MainApplication)getActivity().getApplication();
//                Data data = MainApplication.data;
//                data.getCategories();

            }
        });

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EventBus.getDefault().post(new SwitchFragEvent("SignIn Manual"));
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
                if (event.message.contains("Signup success")) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }
                Toast.makeText(getActivity(), event.message, Toast.LENGTH_SHORT).show();
            }
        }
