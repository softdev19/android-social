package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class Errors {

    @SerializedName("full_messages")
    private String[] messages;

    public String[] getMessages() {
        return messages;
    }
}
