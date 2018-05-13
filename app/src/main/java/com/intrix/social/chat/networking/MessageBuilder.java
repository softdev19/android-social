package com.intrix.social.chat.networking;

import android.util.Log;

import com.intrix.social.chat.abstractions.ModelBuilder;
import com.intrix.social.chat.model.Message;
import com.intrix.social.chat.model.Msg;
import com.intrix.social.chat.utils.EmojiMapUtil;

import org.json.JSONObject;

import java.util.regex.Pattern;

/**
 * Created by yarolegovich on 8/4/15.
 */
public class MessageBuilder implements ModelBuilder<Msg, String> {

    private static final String LOG_TAG = MessageBuilder.class.getSimpleName();

    private static final Pattern TO_SUPERVISOR = Pattern.compile("<@[^>]+>");

    private boolean mIsImage;

    private String mChannelId= "";

    public MessageBuilder(String mChannelId) {
        this.mChannelId = mChannelId;
    }

    @Override
    public Msg buildModelObject(JSONObject message, String... params) {
        try {
            if (!message.getString("type").equals("message")) return null;

            Log.d(LOG_TAG, message.toString());

            Msg msg = new Msg();

            String user = message.has("user") ? message.getString("user") :
                    message.has("username") ? message.getString("username") : "";

            String appUserName = params[0];
            String userId = params[1];

            boolean ignore = decideIgnore(message, appUserName);

            if (ignore) return null;

            msg.setSender((user.equalsIgnoreCase(userId) || user.equalsIgnoreCase(appUserName)) ? 0 : 1);

            if (!mIsImage) {
                msg.setMsgType(Message.MESSAGE_TEXT);
                msg.setContent(EmojiMapUtil.replaceCheatSheetEmojis(message.getString("text")));
                if(message.has("username"))
                    msg.setUsername(message.getString("username"));
            } else {
                msg.setMsgType(Message.MESSAGE_IMAGE);
                msg.setContent(message.getJSONObject("file").getString("url"));
            }

            String ts = message.getString("ts");
            msg.setTimeString(ts);
            msg.setTimestamp(Long.parseLong(ts.replaceAll("\\..*", "")) * 1000);
            msg.setChannelId(mChannelId);

            return msg;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean decideIgnore(JSONObject message, String user) {
        try {
            boolean ignore = false;
            mIsImage = false;

            if (message.has("subtype")) {
                ignore = true;
                if (message.has("file")) {
                    JSONObject file = message.getJSONObject("file");
                    if (file.has("filetype")) {
                        if (file.getString("mimetype").contains("image")) {
                            ignore = false;
                            mIsImage = true;
                        }
                    }
                } else {
                    ignore = !(message.getString("subtype").equals("bot_message") &&
                            message.getString("username").equals(user));
                }
            }

            if (!mIsImage) ignore = TO_SUPERVISOR.matcher(message.getString("text")).find();

            return ignore;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Error when decideIgnore " + e.getMessage(), e);
            return true;
        }
    }
}
