package com.intrix.social.chat.abstractions;

import android.content.Context;

import com.bumptech.glide.Glide;
import com.intrix.social.MainApplication;
import com.intrix.social.R;
import com.intrix.social.chat.adapters.ChatViewHolder;
import com.intrix.social.chat.model.Message;
import com.intrix.social.chat.model.Msg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by yarolegovich on 7/29/15.
 */
public abstract class MessageProcessor {

    private final SimpleDateFormat mDateFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());

    public void displayMessage(Context context, ChatViewHolder viewHolder) {
        Msg message = viewHolder.getMessage();

        viewHolder.setTimeStampText(mDateFormat.format(new Date(message.getTimestamp())));

        if (Message.MESSAGE_USER == message.getSender())
            Glide.with(context).load(MainApplication.data.profilePicUrl)
                    .placeholder(R.drawable.no_image)
                    .into(viewHolder.getUserImage());
    }
}
