package com.intrix.social;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.CustomerRequest;
import com.intrix.social.utils.Toaster;
import com.mikepenz.materialdrawer.view.BezelImageView;

import de.greenrobot.event.EventBus;

/**
 * Created by toanpv on 11/30/15.
 */
public class ThingsLoveActivity extends AppCompatActivity {
    String love1 = "";
    String love2 = "";
    String love3 = "";
    EditText lovebox1;
    EditText lovebox2;
    EditText lovebox3;

    Data data;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_things_love);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayShowCustomEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        data = MainApplication.data;

        lovebox1 = (EditText)findViewById(R.id.love_1);
        lovebox2 = (EditText)findViewById(R.id.love_2);
        lovebox3 = (EditText)findViewById(R.id.love_3);

        lovebox1.setText(data.loadData("user.love1"));
        lovebox2.setText(data.loadData("user.love2"));
        lovebox3.setText(data.loadData("user.love3"));

        EventBus.getDefault().register(this);
    }

    private View getHeader() {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.dash_header, null);
        BezelImageView pic = (BezelImageView) header.findViewById(R.id.profile_pic);
        return header;
    }

    public void onClickGetStarted(View view) {

        love1 = lovebox1.getText().toString();
        love2 = lovebox2.getText().toString();
        love3 = lovebox3.getText().toString();

        if(love1.length() == 0 || love2.length() == 0 || love3.length() == 0)
            Toaster.toast(this, "There's gotta be atleast three things you love :(");
        else if(love1.length() < 2 || love2.length() < 2 || love3.length() < 2)
        {
            Toaster.toast(this, "Don't try to fake it ;)");
        }
        else {
            MainApplication.data.saveData("user.love1", love1);
            MainApplication.data.saveData("user.love2", love2);
            MainApplication.data.saveData("user.love3", love3);
            processData();

        }
    }

    public void processData()
    {
        //"fblink":null,"twtlink":null,"belink":null,"drlink":null,"sclink":null,"otherlinks":null
        CustomerMini customer = new CustomerMini();
//        String desc = data.loadData("user.description");
//        customer.setDescription(desc);
        customer.setInterest1(data.loadData("user.love1"));
        customer.setInterest2(data.loadData("user.love2"));
        customer.setInterest3(data.loadData("user.love3"));


//        customer.setFblink(data.loadData("user.fblink"));
//        customer.setTwtlink(data.loadData("user.twtlink"));
//        customer.setBelink(data.loadData("user.belink"));
//        customer.setDrlink(data.loadData("user.drlink"));
//        customer.setSclink(data.loadData("user.sclink"));
//        customer.setOtherlink(data.loadData("user.otherlinks"));
//
//        customer.setPic(data.getProfilePicUrl());

        CustomerRequest customerRequest = new CustomerRequest(customer);

//        int userId = 1;
//        if(data.getCustomerId().length()>0)
//            userId = Integer.parseInt(data.getCustomerId());

        Networker.getInstance().updateUser(customerRequest, data.user.getId());
        showProgressDialog("Saving your info");
    }

    public void onEvent(NetworkEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        if (event.event.contains("updateUser")) {
            backStackString = "updateUser";
            if(mDialog != null)
                mDialog.dismiss();

            if (event.status) {
                startActivity(new Intent(this, DiscoverSelfieActivity.class));
                finish();
            } else
                Toaster.toast("Unable to update user data. Try Again.", true);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void showProgressDialog(String message) {
        if(mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }


}
