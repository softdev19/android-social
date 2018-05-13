package com.intrix.social.chat.model;

/**
 * Created by yarolegovich on 7/14/15.
 */
public class Message {

    public static final int MESSAGE_USER     = 0;
    public static final int MESSAGE_RESPONSE = 1;

    public static final int MESSAGE_TEXT  = 0;
    public static final int MESSAGE_IMAGE = 1;

    private int sender;
    private int msgType = 0; // 0 - string; 1 - image
    private String mContent;
    private long timestamp;
    private String userid;
    private boolean self;

    public Message(int sndr, String content) {
        sender = sndr;
        mContent = content;
    }

    public Message(int sndr,int type, String content) {
        sender = sndr;
        mContent = content;
        msgType = type;
    }

    public int getSender() {
        return sender;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getContent() {
        return mContent;
    }

    public String getUserid() {
        return userid;
    }

    public int getMsgType() {
        return msgType;
    }

    public void setMsgType(int msgType) {
        this.msgType = msgType;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public boolean isSelf() {
        return self;
    }

    public void setSelf(boolean self) {
        this.self = self;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
