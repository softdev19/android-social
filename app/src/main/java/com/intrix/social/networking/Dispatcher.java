package com.intrix.social.networking;

import static com.intrix.social.networking.NetworkConfigs.SIGN_UP;
import static com.intrix.social.networking.NetworkConfigs.SIGN_IN;

/**
 * Created by yarolegovich on 8/6/15.
 */
public class Dispatcher implements Runnable {

    private com.intrix.social.networking.Networker mNetworker;

    private String command;
    private String[] params;

    public Dispatcher(Networker networker) {
        mNetworker = networker;
    }

    @Override
    public void run() {
        switch (command) {
            case SIGN_UP: mNetworker.signUp(params[0], params[1], params[2]);                      break;
            case SIGN_IN: mNetworker.signIn(params[0], params[1]);                      break;
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