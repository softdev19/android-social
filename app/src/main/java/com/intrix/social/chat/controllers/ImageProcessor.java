package com.intrix.social.chat.controllers;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.intrix.social.chat.abstractions.MessageProcessor;
import com.intrix.social.chat.adapters.ChatViewHolder;
import com.intrix.social.chat.model.Msg;

/**
 * Created by yarolegovich on 7/29/15.
 */
public class ImageProcessor extends MessageProcessor {
    @Override
    public void displayMessage(Context context, ChatViewHolder viewHolder) {
        super.displayMessage(context, viewHolder);

        Msg message = viewHolder.getMessage();

        Glide.with(context).load(message.getContent()).into(viewHolder.getMessageImage());

        ImageView image = viewHolder.getMessageImage();

        image.setVisibility(View.VISIBLE);
        image.setOnClickListener(viewHolder);

        viewHolder.getMessageContent().setVisibility(View.GONE);

    }
}
