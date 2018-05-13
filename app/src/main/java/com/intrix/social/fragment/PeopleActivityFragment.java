package com.intrix.social.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.intrix.social.Data;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.UniversalActivity;
import com.intrix.social.adapter.ActivitiesAdapter;
import com.intrix.social.chat.fragments.NewChatFragment;
import com.intrix.social.model.Connection;
import com.intrix.social.model.Customer;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class PeopleActivityFragment extends Fragment {

    private static final String TAG = "PeopleActivityFragment";
    private Realm mRealm;
    Context mContext;
    Customer selectedUser;
    Connection selectedConnection;
    Data data;
    int myId = 0;

    public PeopleActivityFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new PeopleActivityFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mContext = getActivity();
        mRealm = Realm.getInstance(getActivity());
        EventBus.getDefault().register(this);
        data = MainApplication.data;
        myId = data.user.getId();

        View v = inflater.inflate(R.layout.content_my_people, container, false);
        setActivities(v);
        return v;
    }

    public void setActivities(View v)
    {
        connections = getConnections(mRealm);
        updateConnectionData(mRealm);

        ActivitiesAdapter adapter = new ActivitiesAdapter(getActivity(), (ArrayList)connectionsFinal, (ArrayList)usersFinal);
        ListView listView = (ListView) v.findViewById(R.id.people_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = usersFinal.get(position);
            selectedConnection = connectionsFinal.get(position);
//            Toast.makeText(getActivity(),
//                    "Click ListItem Number " + position + "  -  id = " + usersFinal.get(position).getId(), Toast.LENGTH_LONG)
//                    .show();

   //         showConnectConfirm();
        });
    }

    List<Connection> connections;
    public List<Connection> getConnections(Realm realm) {
        List<Connection> result = new ArrayList<>();
        //int custId = Integer.parseInt(MainApplication.data.getCustomerId());

        String custIdS = ""+data.user.getId();;
        result = realm.allObjects(Connection.class);
        Log.i(TAG,"Allll ---- "+result.toString());
        //result = realm.where(Connection.class).equalTo("customer_id", custId).notEqualTo("status","connected").findAll();
        result = realm.where(Connection.class).equalTo("poi_id", custIdS).or().equalTo("customer_id", myId).notEqualTo("status", "connected").findAll();
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
            Customer cust = realm.where(Customer.class).equalTo("id", conn.getCustomer_id()).or().equalTo("id", Integer.parseInt(conn.getPoi_id())).findFirst();
            if(cust != null && cust.getId() != myId ) {
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
        alertDialog.setTitle("Chat");

        //Setting Dialog Message
        alertDialog.setMessage("Initiate chat with "+ selectedUser.getName());// + " wants to connect with you. Do you accept?");

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

//                ConnectRQ connectRQ = new ConnectRQ();
//                connectRQ.setStatus("connected");
//                Log.i(TAG, "Connection id - " + selectedConnection.getId());
//                Networker.getInstance().connectConfirm(connectRQ, selectedConnection.getId());

                if(selectedConnection.getChat_id() != null && selectedConnection.getChat_id().length()>0) {
                //    MainApplication.data.newChat = selectedConnection.getChat_id();
                 //   startChat();
                }else
                {
                    Toaster.toast("No chat for this user");
                }

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
            backStackString = "updateUser";
            if (event.status) {
                setActivities(getView());
                Toaster.toast("connectConfirm - success", true);
            } else
                Toaster.toast("connectConfirm - failure", true);
        }
    }

    @Override
    public void onDestroyView() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    public void startChat() {
        Intent intent = new Intent(getActivity(), UniversalActivity.class);
        intent.putExtra(UniversalActivity.EXTRA_TOKEN, NewChatFragment.class);
        startActivity(intent);
    }
}