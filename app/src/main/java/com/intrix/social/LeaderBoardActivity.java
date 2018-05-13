package com.intrix.social;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.intrix.social.adapter.RankAdapter;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.utils.FontCache;
import com.intrix.social.utils.Toaster;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.view.BezelImageView;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

/**
 * Created by toanpv on 12/1/15.
 */
public class LeaderBoardActivity extends AppCompatActivity implements View.OnClickListener {
    View menuHeader;
    private Drawer mDrawer;
    Data data;
    boolean filtered = false;
    RankAdapter adapter;
    ListView listView;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        //((TextView) toolbar.findViewById(android.R.id.title)).setText("Deepika Padukone");
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(true);

        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });



        EventBus.getDefault().register(this);
        data = MainApplication.data;

        CollapsingToolbarLayout toolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);
        Typeface tf = FontCache.get("fonts/montana-regular.otf", this);
        toolbarLayout.setCollapsedTitleTypeface(tf);
        toolbarLayout.setExpandedTitleTypeface(tf);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.splash_accent)));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getRankings();
            }
        });

        getRankings();

    }

    private View getHeader() {
        LayoutInflater inflater = getLayoutInflater();
        RelativeLayout header = (RelativeLayout) inflater.inflate(R.layout.dash_header, null);
        BezelImageView pic = (BezelImageView) header.findViewById(R.id.profile_pic);
        return header;
    }

    public void onClickPeople(View v) {
        startActivity(new Intent(this, PeopleActivity.class));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
    }

    public void onEvent(NetworkEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        if (event.event.contains("getLeaderboard")) {
            hideProgress();
            backStackString = "getLeaderboard";
            if (event.status) {
                //Toaster.toast("getLeaderboard - success", true);
                refreshList();
//                if(MainApplication.data.searchResult != null && MainApplication.data.searchResult.size() >0) {
//                    startActivity(new Intent(getActivity(), ResultPeopleActivity.class));
//                }else
//                    Toaster.showToast("Zero users found :(");
            } else
                Toaster.toast("Unable to contact server. Please try again later", true);
        }
    }

    void refreshList()
    {
        final ArrayList<CustomerMini> arrayOfUsers = (ArrayList<CustomerMini>) MainApplication.data.rankResult;
        if(arrayOfUsers.size() == 0) {
            Snackbar.make(listView, "No ranks found. Please try again", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            return;
        }

            adapter = new RankAdapter(this, arrayOfUsers);
        listView = (ListView) findViewById(R.id.people_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                Toast.makeText(getApplicationContext(),
//                        "Click ListItem Number " + position +"  -  id = "+ arrayOfUsers.get(position).getId(), Toast.LENGTH_LONG)
//                        .show();

//                data.selectedCustomer = arrayOfUsers.get(position);
//                startActivity(new Intent(LeaderBoardActivity.this, PeopleActivity.class));
            }
        });
    }

    private void showProgressDialog(String message) {
        if (mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
        //mDialog.setOnDismissListener();
    }

    private void hideProgress() {
        if (mDialog != null)
            mDialog.dismiss();
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    void getRankings()
    {
        showProgressDialog("Getting rankings...");
        Networker.getInstance().getLeaderboard();

    }

}
