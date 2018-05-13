package com.intrix.social.model.event;

/**
 * Created by sutharsha on 05/01/16.
 */
public class ChannelEvent {
    public final String event;
    public final String info;
    public final boolean status;


    public ChannelEvent(String event, boolean status, String info) {
        this.event = event;
        this.status = status;
        this.info = info;
    }
}
