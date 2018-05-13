package com.intrix.social;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.intrix.social.fragment.DiscoveringFragment;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.CustomerRequest;
import com.intrix.social.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

/**
 * Created by toanpv on 11/30/15.
 */
public class DiscoverTalentsActivity extends AppCompatActivity {

    ImageView btns[] = new ImageButton[22];
    int btnIds[] = {R.id.btn11,R.id.btn12,R.id.btn13,R.id.btn14,R.id.btn15,
            R.id.btn21,R.id.btn22,R.id.btn23,R.id.btn24,R.id.btn25,
            R.id.btn31,R.id.btn32,R.id.btn33,R.id.btn34,R.id.btn35,
            R.id.btn41,R.id.btn42,R.id.btn43,R.id.btn44,R.id.btn45,
            R.id.btn51};

    String talentLinks[] = new String[22];

    Data data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_talents);
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
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.splash_accent)));
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(DiscoverTalentsActivity.this, ResultPeopleActivity.class));
//            }
//        });

        EventBus.getDefault().register(this);

        final ImageButton btn = (ImageButton)findViewById(R.id.btn11);

        GridLayout mlayout = (GridLayout)findViewById(R.id.talent_holder);
        int count = mlayout.getChildCount();
        for(int i = 0 ; i <count ; i++){
            final int btnId = i;
            btns[i] = (ImageButton)mlayout.getChildAt(i);
            /*
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toaster.toast("Btn id "+btnId, true);

                    LayoutInflater li = LayoutInflater.from(DiscoverTalentsActivity.this);
                    View promptsView = li.inflate(R.layout.talent_propmt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            DiscoverTalentsActivity.this);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    String currentLinkText = MainApplication.data.loadData("user.talentlink_"+btnId);
                    userInput.setText(currentLinkText);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input and set it to result
                                            // edit text
                                            //result.setText(userInput.getText());
                                            String talentLink = userInput.getText().toString();
                                            if(talentLink.length() > 0) {
                                                talentLinks[btnId] = talentLink;
                                                MainApplication.data.saveData("user.talentlink_" + btnId, talentLink);
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
        */
        }


        PackageManager packageManager;
        List<ResolveInfo> listApps; //this list store all app in device

        List<String> packages = new ArrayList<>(); //this list store all app in device

            packageManager = getPackageManager();
            Intent filterApp = new Intent(Intent.ACTION_MAIN);
            filterApp.addCategory(Intent.CATEGORY_LAUNCHER);
            listApps = packageManager.queryIntentActivities(filterApp,
                    PackageManager.GET_META_DATA);

            for (ResolveInfo app : listApps) {
                //jsonArrayPakages.put(app.activityInfo.packageName.trim());
                if(!isBadPackage(app))
                    packages.add(app.activityInfo.packageName.trim());
            }

        Networker.getInstance().getApps(packages);
    }

    private boolean isBadPackage(ResolveInfo resolveInfo) {
        return ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM & ApplicationInfo.FLAG_IS_GAME) != 0);
    }

    public void onClickGetStarted(View view) {

        for(int i = 0; i < 22; i++) {
            talentLinks[i] = data.loadData("user.talentlink_" + i);
        }

                processData();

        //if(dataPresent)
        //    startActivity(new Intent(this, DiscoveringActivity.class));
/*
        Intent i = new Intent(this, UniversalActivity.class);
        i.putExtra(UniversalActivity.EXTRA_TOKEN, DiscoveringFragment.class);
        startActivity(i);
*/
            //finish();
//        else
//            startActivity(new Intent(this, DescriptionActivity.class));
    }

    public void processData()
    {
        //"fblink":null,"twtlink":null,"belink":null,"drlink":null,"sclink":null,"otherlinks":null
        CustomerMini customer = new CustomerMini();
        String desc = data.loadData("user.description");
        String desc2 = data.loadData("user.description2");
        String desc3 = data.loadData("user.description3");
        customer.setDescription(desc);
        customer.setDescription2(desc2);
        customer.setDescription3(desc3);
        customer.setInterest1(data.loadData("user.love1"));
        customer.setInterest2(data.loadData("user.love2"));
        customer.setInterest3(data.loadData("user.love3"));

        String otherLinks = "";

        for(int i = 0; i < 22; i++)
        {
            if(talentLinks[i].toLowerCase().contains("facebook"))
            {
                data.saveData("user.fblink", talentLinks[i]);
            }else if(talentLinks[i].toLowerCase().contains("twitter"))
            {
                data.saveData("user.twtlink", talentLinks[i]);
            }else if(talentLinks[i].toLowerCase().contains("behance"))
            {
                data.saveData("user.belink", talentLinks[i]);
            }else if(talentLinks[i].toLowerCase().contains("dropbox"))
            {
                data.saveData("user.drlink", talentLinks[i]);
            }else if(talentLinks[i].toLowerCase().contains("soundcloud"))
            {
                data.saveData("user.sclink", talentLinks[i]);
            }else
            {
                if(talentLinks[i].trim().length()>5)
                    otherLinks = otherLinks +"||"+ talentLinks[i].trim();
                data.saveData("user.otherlinks", otherLinks);
            }
        }

        customer.setFblink(data.loadData("user.fblink"));
        customer.setTwtlink(data.loadData("user.twtlink"));
        customer.setBelink(data.loadData("user.belink"));
        customer.setDrlink(data.loadData("user.drlink"));
        customer.setSclink(data.loadData("user.sclink"));
        customer.setOtherlink(data.loadData("user.otherlinks"));

        customer.setPic(data.getProfilePicUrl());

        CustomerRequest customerRequest = new CustomerRequest(customer);

        int userId = 1;
        if(data.getCustomerId().length()>0)
          userId = Integer.parseInt(data.getCustomerId());

        Networker.getInstance().updateUser(customerRequest, userId);
    }

    public void onEvent(NetworkEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        if (event.event.contains("updateUser")) {
            backStackString = "updateUser";
            if (event.status) {
                Toaster.toast("updateUser - success", true);
                Intent i = new Intent(this, UniversalActivity.class);
                i.putExtra(UniversalActivity.EXTRA_TOKEN, DiscoveringFragment.class);
                startActivity(i);
                finish();
            } else
                Toaster.toast("updateUser - failure", true);
        } else if  (event.event.contains("getApps")) {
            if (event.status) {
                Toaster.toast("getApps - success", true);
                reporcessApps();
            } else
                Toaster.toast("getApps - failure", true);
        }
    }


    public void reporcessApps()
    {

        GridLayout mlayout = (GridLayout)findViewById(R.id.talent_holder);
        int count = mlayout.getChildCount();
        for(int i = 0 ; i <count ; i++){
            final int btnId = i;
            btns[i] = (ImageView)mlayout.getChildAt(i);

            if(i < data.socialApps.size()) {
                Glide.with(this).load(data.socialApps.get(i).getIcon_url()).into(btns[i]);
                btns[i].setBackground(null);
            }

            else
                btns[i].setVisibility(View.GONE);

            /*
            btns[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Toaster.toast("Btn id "+btnId, true);

                    LayoutInflater li = LayoutInflater.from(DiscoverTalentsActivity.this);
                    View promptsView = li.inflate(R.layout.talent_propmt, null);

                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                            DiscoverTalentsActivity.this);

                    // set prompts.xml to alertdialog builder
                    alertDialogBuilder.setView(promptsView);

                    final EditText userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    String currentLinkText = MainApplication.data.loadData("user.talentlink_"+btnId);
                    userInput.setText(currentLinkText);

                    // set dialog message
                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog,int id) {
                                            // get user input and set it to result
                                            // edit text
                                            //result.setText(userInput.getText());
                                            String talentLink = userInput.getText().toString();
                                            if(talentLink.length() > 0) {
                                                talentLinks[btnId] = talentLink;
                                                MainApplication.data.saveData("user.talentlink_" + btnId, talentLink);
                                            }
                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                }
            });
            */
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
