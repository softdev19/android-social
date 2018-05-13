package com.intrix.social.chat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionButton;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.chat.adapters.NewChatAdapter;
import com.intrix.social.chat.model.Msg;
import com.intrix.social.chat.networking.NetworkConfigs;
import com.intrix.social.chat.networking.Networker;
import com.intrix.social.chat.utils.ChatUtils;
import com.intrix.social.model.Cart;
import com.intrix.social.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by yarolegovich on 04.01.2016.
 */
public class NewChatFragment extends Fragment implements RealmChangeListener, View.OnClickListener {

    private NewChatAdapter mAdapter;
    //private EditText mMessage;

    private boolean mNeedFullReload = true; // added = true; 2016-01-17
    private EditText mMessageField;

    private Thread mMsgCollector;

    private Realm mRealm;

    ListView mList;

    FloatingActionButton fab_takeOffline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRealm = Realm.getInstance(getActivity());
        mRealm.addChangeListener(this);
        NetworkConfigs.USER_NAME = MainApplication.data.getUserName();
        NetworkConfigs.CHANNEL_ID = MainApplication.data.selectedChat.getName();
        Networker.getInstance().reInitHandler(); // to set for current channel
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat_new, container, false);

        mList = (ListView) v.findViewById(android.R.id.list);
        List<Msg> query = mRealm.where(Msg.class).equalTo("channelId", NetworkConfigs.CHANNEL_ID).findAllSorted("timestamp", Sort.ASCENDING);

        List<Msg> data = new ArrayList<>(query);
        mAdapter = new NewChatAdapter(getActivity(), data);
        mList.setAdapter(mAdapter);


        mMessageField = (EditText) v.findViewById(R.id.message);

        v.findViewById(R.id.send).setOnClickListener(this);
        v.findViewById(R.id.back).setOnClickListener(this);
        v.findViewById(R.id.take_offline_fab).setOnClickListener(this);

        return v;
    }

    ///* COMMENTED 2016-01-17
    private void scrollMyListViewToBottom() {
        mList.post(new Runnable() {
            @Override
            public void run() {
                // Select the last row so it will scroll into view...
                mList.setSelection(mAdapter.getCount() - 1);
            }
        });
    }
    //*/


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send:
                String messageContent = mMessageField.getText().toString();
                if (!messageContent.equals("")) {
                    mMessageField.setText("");
                    Msg message = ChatUtils.createUserMessage(messageContent, NetworkConfigs.CHANNEL_ID);
                    mAdapter.add(message);
                    Networker.getInstance().sendMessage(messageContent);
                    scrollMyListViewToBottom();
                }

                break;
            case R.id.back:
                getActivity().finish();
                break;
            case R.id.take_offline_fab:
                if(Cart.instance().getTableNo() <= 0)
                {
                    Snackbar.make(v, "Please try again after opening a table. The hostess will guide you through it.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }else
                {
                    String offlineMessage = "I invite you to table "+ Cart.instance().getTableNo()+ ". Please ask the waiter to guide you to table "+Cart.instance().getTableNo()+" if you would like to.";
                    Msg message = ChatUtils.createUserMessage(offlineMessage, NetworkConfigs.CHANNEL_ID);
                    mAdapter.add(message);
                    Networker.getInstance().sendMessage(offlineMessage);
                    scrollMyListViewToBottom();
                }
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Utils.hideToolbar(context);
    }

//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        Utils.showToolbar(getActivity());
//    }


    @Override
    public void onResume() {
        super.onResume();
        mMsgCollector = new MessageCollector();
        mMsgCollector.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMsgCollector != null) {
            mMsgCollector.interrupt();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.removeAllChangeListeners();
        mRealm.close();
    }

    @Override
    public void onChange() {
        RealmResults<Msg> messages = mRealm.where(Msg.class).equalTo("channelId", NetworkConfigs.CHANNEL_ID)
                .findAllSorted("timestamp", Sort.ASCENDING);
        int messagesSize = messages.size();
        if (mNeedFullReload) {
            mAdapter.clear();
            mAdapter.addAll(messages);
            mNeedFullReload = false;
        } else if (messagesSize > mAdapter.getCount()) {
            /* Commented 2016-01-17
            int newMessageCount = messagesSize - mAdapter.getCount();
            for (int i = 1; i <= newMessageCount; i++) {
                mAdapter.add(messages.get(messagesSize - i));
            }
            */
            // Following 2 lines replace above commented lines.
            for ( int i = mAdapter.getCount(); i < messagesSize; i++)
                mAdapter.add(messages.get(i));
        }
        scrollMyListViewToBottom();
    }

    public class MessageCollector extends Thread {

        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Networker.getInstance().queryMessages();
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (InterruptedException e) { /* NOP */ }
        }
    }


}
