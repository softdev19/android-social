package com.intrix.social.fragment;

import android.app.AlertDialog;
import android.content.Context;
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
import com.intrix.social.adapter.ChatsAdapter;
import com.intrix.social.chat.fragments.NewChatFragment;
import com.intrix.social.model.Connection;
import com.intrix.social.model.Customer;
import com.intrix.social.model.event.NetworkEvent;
import com.intrix.social.networking.Networker;
import com.intrix.social.networking.model.Chat;
import com.intrix.social.utils.Toaster;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;

public class PeopleChatListFragment extends Fragment {

    private static final String TAG = "PeopleChatListFragment";
    private Realm mRealm;
    Context mContext;
    Customer selectedUser;
    Connection selectedConnection;
    Chat selectedChat;
    Data data;
    int myId = 0;

    public PeopleChatListFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new PeopleChatListFragment();
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
        setChats(v);

        Networker.getInstance().getChats(Integer.parseInt(data.getCustomerId()));
        return v;
    }

    public void setChats(View v)
    {
        connections = getConnections(mRealm);
        updateConnectionData(mRealm);

        ChatsAdapter adapter = new ChatsAdapter(getActivity(), (ArrayList)chatsFinal, (ArrayList)usersFinal);
        ListView listView = (ListView) v.findViewById(R.id.people_list);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            selectedUser = usersFinal.get(position);
            selectedChat = chatsFinal.get(position);

            if(selectedChat != null) {
                MainApplication.data.selectedChat = selectedChat;
                Log.i(TAG, "chat obj - " + selectedChat.getId());

                Intent i = new Intent(getActivity(), UniversalActivity.class);
                i.putExtra(UniversalActivity.EXTRA_TOKEN, NewChatFragment.class);
                startActivity(i);
            }
        });
    }

    List<Connection> connections;
    public List<Connection> getConnections(Realm realm) {
        List<Connection> result = new ArrayList<>();
        int custId = Integer.parseInt(MainApplication.data.getCustomerId());
        result = realm.allObjects(Connection.class);
        Log.i(TAG, "Allll ---- " + result.toString());
        result = realm.where(Connection.class).equalTo("customer_id", custId).equalTo("status", "connected").findAll();
        Log.i(TAG,result.toString());

        return result;
    }

    List<Connection> connectionsFinal;
    List<Chat> chatsFinal;
    List<Customer> usersFinal;

    public void updateConnectionData(Realm realm) {
        List<Customer> result = new ArrayList<>();

        List<Chat> allChats = realm.allObjects(Chat.class);

        result = realm.allObjects(Customer.class);
        Log.i(TAG,"Allll ---- "+result.toString());

        usersFinal = new ArrayList<>();
        connectionsFinal = new ArrayList<>();
        chatsFinal = new ArrayList<>();

        boolean person1 = true;
        int pId = 0;
        for(Chat chat : allChats) {
            pId = Integer.parseInt(chat.getPerson_1_id());
            if (pId == myId) {
                person1 = false;
                pId = Integer.parseInt(chat.getPerson_2_id());
            }

            Customer cust = realm.where(Customer.class).equalTo("id", pId).findFirst();
            if(cust != null) {
                Log.i(TAG, " -- "+pId);
                chatsFinal.add(chat);
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
        alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> {

//                ConnectRQ connectRQ = new ConnectRQ();
//                connectRQ.setStatus("connected");
//                Log.i(TAG, "Connection id - " + selectedConnection.getId());
//                Networker.getInstance().connectConfirm(connectRQ, selectedConnection.getId());

            if (selectedConnection.getChat_id() != null && selectedConnection.getChat_id().length() > 0) {
                //    MainApplication.data.newChat = selectedConnection.getChat_id();
                //   startChat();
            } else {
                Toaster.toast("No chat for this user");
            }

        });

        //On pressing cancel button
        alertDialog.setNegativeButton(R.string.cancel, (dialog, which) -> {
            dialog.cancel();
        });

        alertDialog.show();
    }

    public void onEvent(NetworkEvent event) {
        if (event.event.contains("connectConfirm")) {
            if (event.status) {
                setChats(getView());
                Toaster.toast("connectConfirm - success", true);
            } else
                Toaster.toast("connectConfirm - failure", true);
        }else if (event.event.contains("getChats")) {
            if (event.status) {
                setChats(getView());
                Toaster.toast("getChats - success", true);
            } else
                Toaster.toast("getChats - failure", true);
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