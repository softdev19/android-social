package com.intrix.social.chat.controllers;

import android.content.Context;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.View;

import com.intrix.social.chat.abstractions.MessageProcessor;
import com.intrix.social.chat.adapters.ChatViewHolder;
import com.intrix.social.chat.model.Msg;

/**
 * Created by yarolegovich on 7/29/15.
 */
public class PlainTextProcessor extends MessageProcessor {

    private static final String LOG_TAG = PlainTextProcessor.class.getSimpleName();

    @Override
    public void displayMessage(Context context, ChatViewHolder viewHolder) {
        super.displayMessage(context, viewHolder);


        Msg message = viewHolder.getMessage();

        String content = message.getContent();

        if (content.length() > 0) {
            viewHolder.getMessageContent().setAutoLinkMask(Linkify.ALL);
            viewHolder.setMessageContent(content);
            viewHolder.getMessageContent().setMovementMethod(LinkMovementMethod.getInstance());
        }

        viewHolder.getMessageImage().setVisibility(View.GONE);
        viewHolder.getMessageContent().setVisibility(View.VISIBLE);
    }
}
