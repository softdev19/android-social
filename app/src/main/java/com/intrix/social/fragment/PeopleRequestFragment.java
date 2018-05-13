package com.intrix.social.fragment;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.intrix.social.Data;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.adapter.ConnectionsAdapter;
import com.intrix.social.model.Connection;
import com.intrix.social.model.Customer;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.ChatRQ;
import com.intrix.social.networking.model.ConnectRQ;
import com.intrix.social.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class PeopleRequestFragment extends Fragment {
    private static final String TAG = "PeopleRequestFragment";
    private Realm mRealm;
    Context mContext;
    Customer selectedUser;
    Connection selectedConnection;
    private BroadcastReceiver receiver;
    Data data;

    public PeopleRequestFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new PeopleRequestFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mContext = getActivity();
        data = MainApplication.data;
        mRealm = Realm.getInstance(getActivity());
        EventBus.getDefault().register(this);

        IntentFilter filter = new IntentFilter();
        filter.addAction("CHANNEL_CREATED");
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                switch (intent.getAction()) {
                    case "CHANNEL_CREATED": {
                        boolean action_status = intent.getBooleanExtra("status", false);
                        if (action_status) {
                            String channelId = intent.getStringExtra("info");

                            Toaster.toast("Channel Created -- id --> "+ channelId, true);

                            ChatRQ chatRQ = new ChatRQ();
                            chatRQ.setName(channelId); // event.info is channel id
                            chatRQ.setPerson_1_id(""+selectedConnection.getCustomer_id());
                            chatRQ.setPerson_2_id(selectedConnection.getPoi_id());
                            chatRQ.setLocation("Church Street");
                            chatRQ.setTable_no("777");

                            Networker.getInstance().chatSetup(chatRQ);


//                            ConnectRQ connectRQ = new ConnectRQ();
//                            connectRQ.setStatus("connected");
//                            connectRQ.setChat_id(channelId);
//                            Log.i(TAG, "Connection id - " + selectedConnection.getId());
//                            Networker.getInstance().connectConfirm(connectRQ, selectedConnection.getId());


                        } else {
                            Toaster.toast("Unable to create Channel. Contact the dev",true);
                        }
                    }
                    break;
                }
            }


        };
        getActivity().registerReceiver(receiver, filter);

        Networker.getInstance().getCustomersAuto();
        Networker.getInstance().getConnectionsAuto();

        View v = inflater.inflate(R.layout.content_my_people, container, false);
        setConnections(v);
        return v;
    }

    public void setConnections(View v)
    {
        connections = getConnections(mRealm);
        updateConnectionData(mRealm);

        ConnectionsAdapter adapter = new ConnectionsAdapter(getActivity(), (ArrayList)connectionsFinal, (ArrayList)usersFinal);
        ListView listView = (ListView) v.findViewById(R.id.people_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = usersFinal.get(position);
            selectedConnection = connectionsFinal.get(position);
//            Toast.makeText(getActivity(),
//                    "Click ListItem Number " + position + "  -  id = " + usersFinal.get(position).getId(), Toast.LENGTH_LONG)
//                    .show();
            showConnectConfirm();
        });
    }

    List<Connection> connections;
    public List<Connection> getConnections(Realm realm) {
        List<Connection> result = new ArrayList<>();
        //int custId = Integer.parseInt(MainApplication.data.getCustomerId());
        String custId = MainApplication.data.getCustomerId();
        result = realm.allObjects(Connection.class);
        Log.i(TAG,"Allll ---- "+result.toString());
        result = realm.where(Connection.class).equalTo("poi_id", custId).notEqualTo("status","connected").findAll();
        Log.i(TAG,result.toString());

        return result;
    }

    List<Connection> connectionsFinal;
    List<Customer> usersFinal;

    public void updateConnectionData(Realm realm) {
        List<Customer> result = new ArrayList<>();

        result = realm.allObjects(Customer.class);
        Log.i(TAG, "Allll ---- " + result.toString());

        usersFinal = new ArrayList<>();
        connectionsFinal = new ArrayList<>();

        for(Connection conn : connections) {
           // Customer cust = realm.where(Customer.class).equalTo("id", Integer.parseInt(conn.getPoi_id())).findFirst();
            Customer cust = realm.where(Customer.class).equalTo("id", conn.getCustomer_id()).findFirst();
            if(cust != null) {
                Log.i(TAG, conn.getPoi_id());
                connectionsFinal.add(conn);
                usersFinal.add(cust);
            }
        }
    }


    public void showConnectConfirm()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);

        //Setting Dialog Title
        alertDialog.setTitle(R.string.connect);

        //Setting Dialog Message
        alertDialog.setMessage(selectedUser.getName() + " wants to connect with you. Do you accept?");

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                ConnectRQ connectRQ = new ConnectRQ();
//                connectRQ.setStatus("connected");
//                Log.i(TAG, "Connection id - " + selectedConnection.getId());
//                Networker.getInstance().connectConfirm(connectRQ, selectedConnection.getId());

                String channelName = "CH" + System.currentTimeMillis();
                com.intrix.social.chat.networking.Networker.getInstance().createChannel(channelName);
            }
        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    public void onEvent(NetworkEvent event) {
        Fragment fragment = null;
        String backStackString = "";
        if (event.event.contains("connectConfirm")) {
            if (event.status) {
                Toaster.toast("connectConfirm - success", true);
                setConnections(getView());
                //String channelName = "CH"+System.currentTimeMillis();
                //com.intrix.social.chat.networking.Networker.getInstance().createChannel(channelName);
            } else
                Toaster.toast("connectConfirm - failure", true);
        }else if(event.event.contains("chatSetup"))
        {
            if (event.status) {
                Toaster.toast("connectConfirm - success", true);

                ConnectRQ connectRQ = new ConnectRQ();
                connectRQ.setStatus("connected");
                connectRQ.setChat_id("" + data.newChat.getId());
                Log.i(TAG, "Connection id - " + selectedConnection.getId());
                Networker.getInstance().connectConfirm(connectRQ, selectedConnection.getId());
                //String channelName = "CH"+System.currentTimeMillis();
                //com.intrix.social.chat.networking.Networker.getInstance().createChannel(channelName);
            } else
                Toaster.toast("connectConfirm - failure", true);
        }else if(event.event.contains("getConnections"))
        {
            if (event.status) {
                Toaster.toast("getConnections - success", true);
                setConnections(getView());
            } else
                Toaster.toast("getConnections - failure", true);
        }
    }

//    public void onEvent(ChannelEvent event) {
//        Fragment fragment = null;
//        String backStackString = "";
//        if (event.event.contains("channelCreated")) {
//            backStackString = "chatOne";
//            if (event.status) {
//                Toaster.toast("channelCreated event :| - success", true);
//                String channelName = "CH"+System.currentTimeMillis();
//
//                ChatRQ chatRQ = new ChatRQ();
//                chatRQ.setName(event.info); // event.info is channel id
//                chatRQ.setPerson_1_id(""+selectedConnection.getCustomer_id());
//                chatRQ.setPerson_2_id(selectedConnection.getPoi_id());
//                chatRQ.setLocation("Church Street");
//                chatRQ.setTable_no("999");
//
//            } else
//                Toaster.toast("connectConfirm - failure", true);
//        }
//    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        try {
            if (receiver != null)
                getActivity().unregisterReceiver(receiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }


//    @Override
//    public void onDestroy() {
//        EventBus.getDefault().unregister(this);
//        try {
//            if (receiver != null)
//                getActivity().unregisterReceiver(receiver);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        super.onDestroy();
//    }

}