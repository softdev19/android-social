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

import de.greenrobot.event.EventBus;
import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by toanpv on 11/30/15.
 */
public class DescriptionActivity extends AppCompatActivity {

    String userDesc = "";
    String userDesc2 = "";
    EditText descBox;
    EditText descBox2;
    Data data;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        data = MainApplication.data;
        setContentView(R.layout.activity_description_2);
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

        descBox = (EditText) findViewById(R.id.self_desc);
//        String userDesc = descBox.getText().toString();
        descBox.setText(MainApplication.data.loadData("user.description2"));

        descBox2 = (EditText) findViewById(R.id.self_desc2);
//        String userDesc = descBox.getText().toString();
        descBox2.setText(MainApplication.data.loadData("user.description3"));
        EventBus.getDefault().register(this);
    }

    private View getHeader() {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.dash_header, null);
        CircleImageView pic = (CircleImageView) header.findViewById(R.id.profile_pic);
        return header;
    }

    public void onClickGetStarted(View view) {
        userDesc = descBox.getText().toString();
        userDesc2 = descBox2.getText().toString();
//        if(userDesc.length() == 0)
//             Toaster.toast(this, "Please type your description");
//        else if(userDesc.length() < 10)
//        {
//            Toaster.toast(this, "Don't be shy. Type a bit more :)");
//        }
//        else {

        MainApplication.data.saveData("user.description2", userDesc);
        MainApplication.data.saveData("user.description3", userDesc2);

        processData();
//        }
    }

    public void processData() {
        //"fblink":null,"twtlink":null,"belink":null,"drlink":null,"sclink":null,"otherlinks":null
        CustomerMini customer = new CustomerMini();
        String desc = data.loadData("user.description2");
        String desc2 = data.loadData("user.description3");
        customer.setDescription2(desc);
        customer.setDescription3(desc2);

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
            if (mDialog != null)
                mDialog.dismiss();

            backStackString = "updateUser";
            if (event.status) {
                //Toaster.toast("updateUser - success", true);
                startActivity(new Intent(this, ThingsLoveActivity.class));
                finish();
            } else
                Toaster.toast("updateUser - failure", true);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void showProgressDialog(String message) {
        if (mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }


}
