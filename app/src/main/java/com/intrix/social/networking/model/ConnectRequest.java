package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

public class ConnectRequest {

    @SerializedName("connect_request")
    private ConnectRQ connectRQ;
    public ConnectRequest(ConnectRQ connectRQ) {
        this.connectRQ = connectRQ;
    }
}
