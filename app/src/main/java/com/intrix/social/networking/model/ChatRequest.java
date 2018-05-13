package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

public class ChatRequest {

    @SerializedName("chat")
    private ChatRQ chatRQ;
    public ChatRequest(ChatRQ chatRQ) {
        this.chatRQ = chatRQ;
    }
}
