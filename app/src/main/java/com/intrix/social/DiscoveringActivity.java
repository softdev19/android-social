package com.intrix.social;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.utils.Toaster;

import de.greenrobot.event.EventBus;

/**
 * Created by toanpv on 11/30/15.
 */
public class DiscoveringActivity extends AppCompatActivity {

    private Runnable runnablePtr = null;
    private Handler handler = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discovering_people);
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

        EventBus.getDefault().register(this);

        runnablePtr = new Runnable() {
            @Override
            public void run() {
                //startActivity(new Intent(DiscoveringActivity.this, ResultPeopleActivity.class));
                startActivity(new Intent(DiscoveringActivity.this, PeopleActivity.class));
            }
        };

        handler = new Handler();
        handler.postDelayed(runnablePtr, 5000);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.splash_accent)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(DiscoveringActivity.this, ResultPeopleActivity.class));
                startActivity(new Intent(DiscoveringActivity.this, PeopleActivity.class));
                if (handler != null && runnablePtr != null)
                    handler.removeCallbacks(runnablePtr);
            }
        });
    }

    @Override
    protected void onDestroy() {
        if(handler != null && runnablePtr != null)
            handler.removeCallbacks(runnablePtr);
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        // your code.
        if(handler != null && runnablePtr != null)
            handler.removeCallbacks(runnablePtr);
        super.onBackPressed();
    }

    public void onEvent(NetworkEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        if (event.event.contains("searchCustomers")) {
            backStackString = "searchCustomers";
            if (event.status) {
                Toaster.toast("searchCustomers - success", true);
            } else
                Toaster.toast("searchCustomers - failure", true);
        }
//                fragment = new SignInManualFragment();
//        } else {
//            backStackString = "SignUp Manual";
//            fragment = new SignUpFragment();
//        }
//        if (fragment != null)
//            fm.beginTransaction().replace(R.id.container, fragment).addToBackStack(backStackString).commit();
    }


}
