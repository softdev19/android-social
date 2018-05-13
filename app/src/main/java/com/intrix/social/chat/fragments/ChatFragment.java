package com.intrix.social.chat.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ListView;

import com.github.clans.fab.FloatingActionMenu;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.utils.Toaster;
import com.intrix.social.chat.abstractions.ActivityWithOverlay;
import com.intrix.social.chat.adapters.ChatAdapter;
import com.intrix.social.chat.controllers.FabClickHandler;
import com.intrix.social.chat.controllers.MessageProcessorPool;
import com.intrix.social.chat.model.Msg;
import com.intrix.social.chat.networking.NetworkConfigs;
import com.intrix.social.chat.networking.Networker;
import com.intrix.social.chat.networking.PhotoUploadHandler;
import com.intrix.social.chat.utils.ChatUtils;
import com.intrix.social.chat.utils.Utils;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

/**
 * Created by yarolegovich on 7/15/15.
 */
public class ChatFragment extends Fragment implements View.OnClickListener,
        RealmChangeListener, View.OnTouchListener {

    private static final String LOG_TAG = ChatFragment.class.getSimpleName();

    private boolean mNeedFullReload;

    private ChatAdapter mChatAdapter;
    private EditText mMessageField;

    private Realm mRealm;

    private MessageCollector mMessageCollector;

    private FabClickHandler mFabClickHandler;

    private Networker mNetworker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(this);

        mNetworker = Networker.getInstance();
//        if (!MainApplication.data.loadBooleanData("PurposeSet"))
//            mNetworker.setPurpose();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chat, container, false);

        NetworkConfigs.CHANNEL_ID = MainApplication.data.getChannelId();
        NetworkConfigs.USER_ID = MainApplication.data.getUserId();
        NetworkConfigs.USER_NAME = MainApplication.data.getUserName();

        mMessageField = (EditText) v.findViewById(R.id.message_field);

        ListView chat = (ListView) v.findViewById(R.id.chat);

        List<Msg> cachedMessages = mRealm.where(Msg.class)
                .findAllSorted("timestamp", Sort.ASCENDING);

        Log.d(LOG_TAG, String.valueOf(cachedMessages.size()));
        mChatAdapter = new ChatAdapter.ChatAdapterBuilder(getActivity())
                .withProcessorFactory(new MessageProcessorPool())
                .withOverlay(getOverlayActivity())
                .withData(cachedMessages)
                .build();

        FloatingActionMenu menu = (FloatingActionMenu) v.findViewById(R.id.action_menu);
        mFabClickHandler = new FabClickHandler(menu, v.findViewById(R.id.fragment_overlay));

        chat.setAdapter(mChatAdapter);
        chat.setOnTouchListener(this);

        v.findViewById(R.id.send_message_btn).setOnClickListener(this);
        v.findViewById(R.id.choose_from_gallery).setOnClickListener(this);
        v.findViewById(R.id.take_photo).setOnClickListener(this);

        ChatUtils.sendInfoMessages(this);

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        PhotoUploadHandler.handleFileUploadResult(getActivity(), requestCode, resultCode, data);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mRealm.removeAllChangeListeners();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mMessageCollector != null)
            mMessageCollector.interrupt();
        mFabClickHandler.closeFabMenu();
    }

    @Override
    public void onResume() {
        super.onResume();
        mMessageCollector = new MessageCollector();
        mMessageCollector.start();
    }

    private ActivityWithOverlay getOverlayActivity() {
        try {
            return (ActivityWithOverlay) getActivity();
        } catch (ClassCastException e) {
            return null;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.send_message_btn:
                String messageContent = mMessageField.getText().toString();
                if (!messageContent.equals("")) {
                    mMessageField.setText("");
                    Msg message = ChatUtils.createUserMessage(messageContent, NetworkConfigs.CHANNEL_ID);
                    sendToChat(message);
                    mNetworker.sendMessage(messageContent);
                }
                break;
            case R.id.take_photo:
                if (PhotoUploadHandler.deviceSupportsCamera(getActivity()))
                    PhotoUploadHandler.takePhoto(this);
                else Toaster.showToast("No camera support");
                break;
            case R.id.choose_from_gallery:
                PhotoUploadHandler.loadFromGallery(this);
                break;
        }
    }

    public void requestFullReload() {
        mNeedFullReload = true;
    }

    private String pickRandomName() {
        return "";
    }

    private void sendToChat(Msg message) {
        mChatAdapter.addMessage(message);
    }

    /*
     * Callback method, called when Realm is changed.
     * 1. We query messages
     * 2. We compare the difference in number of messages queried from realm and messages in our chat.
     * 3. If we don't have some messages displayed, we add through adapter.
     */
    @Override
    public void onChange() {
        RealmResults<Msg> messages = mRealm.where(Msg.class).findAllSorted("timestamp", Sort.ASCENDING);
        int messagesSize = messages.size();
        Log.d(LOG_TAG, "in onChange " + messagesSize);
        if (mNeedFullReload) {
            mChatAdapter.clear();
            mChatAdapter.addAll(messages);
            mNeedFullReload = false;
        } else if (messagesSize > mChatAdapter.getCount()) {
            int newMessageCount = messagesSize - mChatAdapter.getCount();
            for (int i = 1; i <= newMessageCount; i++) {
                mChatAdapter.addMessage(messages.get(messagesSize - i));
            }
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Utils.hideSoftwareKeyboard(getActivity());
        return false;
    }

    public class MessageCollector extends Thread {
        @Override
        public void run() {
            try {
                while (!isInterrupted()) {
                    mNetworker.queryMessages();
                    TimeUnit.SECONDS.sleep(2);
                }
            } catch (InterruptedException e) { /* NOP */ }
        }
    }
}
