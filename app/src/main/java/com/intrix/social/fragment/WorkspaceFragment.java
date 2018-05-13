package com.intrix.social.fragment;


import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.intrix.social.R;
import com.intrix.social.common.view.SBtnView;
import com.intrix.social.common.view.SEditText;

/**
 * A simple {@link Fragment} subclass.
 */
public class WorkspaceFragment extends Fragment {

    SEditText userName, phoneNumber, company;
    SBtnView signUp;
    ImageView userImage;


    public WorkspaceFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatActivity a = (AppCompatActivity) getActivity();
        Drawable bg = new ColorDrawable(ContextCompat.getColor(getActivity(), R.color.s_orange));
        a.getSupportActionBar().setBackgroundDrawable(bg);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_workspace, container, false);
        userName = (SEditText) view.findViewById(R.id.et_userName);
        phoneNumber = (SEditText) view.findViewById(R.id.et_phoneNumber);
        company = (SEditText) view.findViewById(R.id.et_company);

        userImage = (ImageView) view.findViewById(R.id.img_userImage);

        signUp = (SBtnView) view.findViewById(R.id.btn_signUp);

        return view;
    }


}
