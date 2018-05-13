package com.intrix.social.chat.networking;

import static com.intrix.social.chat.networking.NetworkConfigs.CHANNELS_HISTORY;
import static com.intrix.social.chat.networking.NetworkConfigs.CHANNELS_INVITE;
import static com.intrix.social.chat.networking.NetworkConfigs.CHAT_POST_MESSAGE;
import static com.intrix.social.chat.networking.NetworkConfigs.CREATE_CHANNEL;
import static com.intrix.social.chat.networking.NetworkConfigs.FILES_UPLOAD;
import static com.intrix.social.chat.networking.NetworkConfigs.SET_PURPOSE;

/**
 * Created by yarolegovich on 8/6/15.
 */
public class Dispatcher implements Runnable {

    private Networker mNetworker;

    private String command;
    private String[] params;

    public Dispatcher(Networker networker) {
        mNetworker = networker;
    }

    @Override
    public void run() {
        switch (command) {
            case CHAT_POST_MESSAGE: mNetworker.sendMessage(params[0], params[1]);           break;
            case CHANNELS_HISTORY:  mNetworker.queryMessages();                             break;
            case FILES_UPLOAD:      mNetworker.uploadFile(params[0]);                       break;
            case CREATE_CHANNEL:    mNetworker.createChannel(params[0]);                    break;
            case SET_PURPOSE:       mNetworker.setPurpose();                                break;
            case CHANNELS_INVITE:   mNetworker.invite(params[0]);                           break;
        }
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String[] getParams() {
        return params;
    }

    public void setParams(String[] params) {
        this.params = params;
    }
}