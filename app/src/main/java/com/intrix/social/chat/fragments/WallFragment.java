package com.intrix.social.chat.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;

import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.chat.adapters.WallAdapter;
import com.intrix.social.chat.model.Msg;
import com.intrix.social.chat.networking.NetworkConfigs;
import com.intrix.social.chat.networking.Networker;
import com.intrix.social.chat.utils.ChatUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by yarolegovich on 24.12.2015.
 */
public class WallFragment extends Fragment implements RealmChangeListener, View.OnClickListener {

    private WallAdapter mAdapter;
    private boolean mNeedFullReload;
    private EditText mMessageField;

    private Thread mMsgCollector;

    private Realm mRealm;

    private ListView mList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        mRealm = Realm.getInstance(getActivity());
        mRealm.addChangeListener(this);
        NetworkConfigs.USER_NAME = MainApplication.data.getUserName();
        Networker.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_wall, container, false);

        mList = (ListView) v.findViewById(android.R.id.list);
        List<Msg> query = mRealm.where(Msg.class).equalTo("channelId", NetworkConfigs.SOCIAL_WALL).findAllSorted("timestamp", Sort.ASCENDING);
        List<Msg> data = new ArrayList<>(query);
        mAdapter = new WallAdapter(getActivity(), data);
        mList.setAdapter(mAdapter);

        mMessageField = (EditText) v.findViewById(R.id.message_field);

        v.findViewById(R.id.send).setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        String messageContent = mMessageField.getText().toString();
        if (!messageContent.equals("")) {
            mMessageField.setText("");
            Msg message = ChatUtils.createUserMessage(messageContent, NetworkConfigs.SOCIAL_WALL);
            mAdapter.add(message);
            mList.smoothScrollByOffset(mAdapter.getCount() - 1);
            Networker.getInstance().sendToWall(messageContent);
        }
    }

    @Override
    public void onChange() {
        RealmResults<Msg> messages = mRealm.where(Msg.class).equalTo("channelId",NetworkConfigs.SOCIAL_WALL )
                .findAllSorted("timestamp", Sort.ASCENDING);
        int messagesSize = messages.size();
        if (mNeedFullReload) {
            mAdapter.clear();
            mAdapter.addAll(messages);
            mNeedFullReload = false;
        } else if (messagesSize > mAdapter.getCount()) {
            int newMessageCount = messagesSize - mAdapter.getCount();
            for (int i = 1; i <= newMessageCount; i++) {
                mAdapter.add(messages.get(messagesSize - i));
            }
        }
        mList.smoothScrollByOffset(mAdapter.getCount() - 1);
    }

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

    public class MessageCollector extends Thread {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    Networker.getInstance().queryWall();
                    TimeUnit.SECONDS.sleep(5);
                }
            } catch (InterruptedException e) { /* NOP */ }
        }
    }
}
