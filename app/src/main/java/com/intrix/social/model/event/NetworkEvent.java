package com.intrix.social.model.event;

/**
 * Created by sutharsha on 24/11/15.
 */
public class NetworkEvent {
    public final String event;
    public final boolean status;

    public NetworkEvent(String event, boolean status) {
        this.event = event;
        this.status = status;
    }
}