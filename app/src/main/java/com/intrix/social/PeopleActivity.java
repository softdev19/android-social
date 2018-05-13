package com.intrix.social;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.intrix.social.chat.fragments.NewChatFragment;
import com.intrix.social.model.Connection;
import com.intrix.social.model.CustomerMini;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.Chat;
import com.intrix.social.networking.model.ConnectRQ;
import com.intrix.social.utils.Toaster;

import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

/**
 * Created by toanpv on 11/30/15.
 */
public class PeopleActivity extends AppCompatActivity {

    CustomerMini user;
    Data data;
    Button connect_btn;
    boolean connected = false;
    boolean initiatedConnReq = true;
    Connection conn = null;
    private Dialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayShowTitleEnabled(false);
        ab.setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        connect_btn = (Button) findViewById(R.id.btn_get_stated);

        EventBus.getDefault().register(this);

        data = MainApplication.data;
        TextView name = (TextView) findViewById(R.id.tv_name_people);
        TextView interest = (TextView) findViewById(R.id.tv_tag_people);
        TextView desctiption = (TextView) findViewById(R.id.tv_interest_people);
        ImageView bgImg = (ImageView) findViewById(R.id.iv_people_full);
        TextView location = (TextView) findViewById(R.id.tv_city_people);

        user = MainApplication.data.selectedCustomer;

        if(user.getCity() != null && user.getCity().length() > 0)
            location.setText(user.getCity());

        if (user == null)
            return;
        name.setText(user.getName());
        //if(user.getInterest1().length() > 0 && user.getInterest1().length() > 0  && user.getInterest1().length() > 0 ) {
        if (user.getInterest1() != null && user.getInterest2() != null && user.getInterest3() != null && user.getInterest1().length() > 0 && user.getInterest1().length() > 0 && user.getInterest1().length() > 0) {
            String interests = "#" + user.getInterest1() + " #" + user.getInterest2() + " #" + user.getInterest3();
            interest.setText(interests);
        } else
            interest.setText("No Interests yet");
        if (user.getDescription() != null)
            desctiption.setText(user.getDescription2());
        else
            desctiption.setText("No Description yet");

        Glide.with(this).load(user.getPic()).error(R.drawable.people_full).into(bgImg);

        List<Connection> allCons = Realm.getDefaultInstance().where(Connection.class).findAll();

        if(data.user.getId() == 0 && data.selectedCustomer != null) {
            conn = Realm.getDefaultInstance().where(Connection.class).equalTo("poi_id", "" + data.selectedCustomer.getId()).equalTo("customer_id", data.user.getId()).findFirst();
            if (conn == null) {
                conn = Realm.getDefaultInstance().where(Connection.class).equalTo("poi_id", "" + data.user.getId()).equalTo("customer_id", data.selectedCustomer.getId()).findFirst();
                initiatedConnReq = false;
            }
        }

        if (conn != null) {
            if (conn.getStatus() != null && conn.getStatus().equalsIgnoreCase("connected")) {
                connected = true;
                connect_btn.setText("Chat");
            } else {
                if (initiatedConnReq) {
                    connect_btn.setText("Connect request sent");
                    connect_btn.setEnabled(false);
                } else
                    connect_btn.setText("Go to requests page ->");
            }
        }

        if(!connected)
        {
            showProgressDialog("Getting status...");
            Networker.getInstance().getConnectionsAuto();
        }

    }

    public void onClickGetStarted(View view) {
        //startActivity(new Intent(this, PeopleDetailActivity.class));
        //Toaster.toast("Connect request sent", true);
        if (connected) {
            Chat chat = Realm.getDefaultInstance().where(Chat.class).equalTo("id", Integer.parseInt(conn.getChat_id())).findFirst();
            if (chat != null) {
                data.selectedChat = chat;
                startChat();
            } else {
                Toaster.toast("Unable to find chat connection. Checking the cloud", true);
                showProgressDialog("Checking cloud");
                Networker.getInstance().getChatsAuto();
            }
        } else {
            ConnectRQ connectRQ = new ConnectRQ();
            connectRQ.setCustomer_id(Integer.parseInt(data.getCustomerId()));
            connectRQ.setLocation(data.user.getCity());
            connectRQ.setPoi_id("" + user.getId());
            Networker.getInstance().connect(connectRQ);
        }
    }

    public void onClickSocial(View view) {
        int id = view.getId();
        switch (id) {
       /*     case R.id.btn_twitter_people:
                Snackbar.make(view, "Twitter link not shared", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                if (user.getTwtlink() != null && user.getTwtlink().length() > 0 && Patterns.WEB_URL.matcher(user.getFblink()).matches()) {
                    String url = user.getTwtlink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                break;
            case R.id.btn_facebook_people:
                if (user.getFblink() != null && user.getFblink().length() > 0 && Patterns.WEB_URL.matcher(user.getFblink()).matches()) {
                    String url = user.getFblink();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                } else {
                    Snackbar.make(view, "Facebook link not shared", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                break;
            */
        }
    }

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("connect")) {
            if (event.status) {
                Toaster.toast("Connect request sent", true);
            } else
                Toaster.toast("Connect request failed :(", true);
        } else if (event.event.contains("getChats")) {
            hideProgress();
            if (event.status) {
                Chat chat = Realm.getDefaultInstance().where(Chat.class).equalTo("id", Integer.parseInt(conn.getChat_id())).findFirst();
                if (chat == null) {
                    Toaster.toast("Your chat connection is lost in the cloud. Contact the customer support", true);
                } else {
                    data.selectedChat = chat;
                    //startChat();
                    updateConnectionStatus();
                }
            } else
                Toaster.toast("Network failure", true);
        }else if (event.event.contains("getConnections")) {
                hideProgress();
            if (event.status) {

                if(data.selectedCustomer != null) {
                    conn = Realm.getDefaultInstance().where(Connection.class).equalTo("poi_id", "" + data.selectedCustomer.getId()).equalTo("customer_id", data.user.getId()).findFirst();
                    if (conn == null) {
                        conn = Realm.getDefaultInstance().where(Connection.class).equalTo("poi_id", "" + data.user.getId()).equalTo("customer_id", data.selectedCustomer.getId()).findFirst();
                        initiatedConnReq = false;
                    }
                }

                if(conn != null) {
                    showProgressDialog("Updating status...");
                    Networker.getInstance().getChatsAuto();
                }
            } else
                Toaster.toast("Network failure", true);
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }


    public void startChat() {
        Intent i = new Intent(this, UniversalActivity.class);
        i.putExtra(UniversalActivity.EXTRA_TOKEN, NewChatFragment.class);
        startActivity(i);
    }

    void updateConnectionStatus()
    {
        if(data.user.getId() == 0 && data.selectedCustomer != null) {
            conn = Realm.getDefaultInstance().where(Connection.class).equalTo("poi_id", "" + data.selectedCustomer.getId()).equalTo("customer_id", data.user.getId()).findFirst();
            if (conn == null) {
                conn = Realm.getDefaultInstance().where(Connection.class).equalTo("poi_id", "" + data.user.getId()).equalTo("customer_id", data.selectedCustomer.getId()).findFirst();
                initiatedConnReq = false;
            }
        }

        if (conn != null) {
            if (conn.getStatus() != null && conn.getStatus().equalsIgnoreCase("connected")) {
                connected = true;
                connect_btn.setText("Chat");
            } else {
                if (initiatedConnReq) {
                    connect_btn.setText("Connect request sent");
                    connect_btn.setEnabled(false);
                } else
                    connect_btn.setText("Go to requests page ->");
            }
        }

    }

    private void showProgressDialog(String message) {
        if(mDialog != null)
            mDialog.dismiss();
        mDialog = new ProgressDialog(this);
        mDialog.setCancelable(false);
        mDialog.setTitle(message);
        mDialog.show();
    }

    private void hideProgress() {
        if(mDialog != null)
            mDialog.dismiss();
    }


}
