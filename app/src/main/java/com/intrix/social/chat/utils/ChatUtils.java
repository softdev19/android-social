package com.intrix.social.chat.utils;

import android.util.Log;

import com.intrix.social.MainApplication;
import com.intrix.social.chat.fragments.ChatFragment;
import com.intrix.social.chat.model.Message;
import com.intrix.social.chat.model.Msg;

import java.util.Calendar;
import java.util.TimeZone;

import io.realm.Realm;

import static com.intrix.social.chat.networking.NetworkConfigs.USER_NAME;

/**
 * Created by yarolegovich on 10.10.2015.
 */
public class ChatUtils {

    private static final String LOG_TAG = ChatUtils.class.getSimpleName();

    public static Msg createAgentMessage(String text, String channelId) {
        return createMessage(text, Message.MESSAGE_RESPONSE, channelId);
    }

    public static Msg createUserMessage(String text, String channel) {
        return createMessage(text, Message.MESSAGE_USER,  channel);
    }

    public static Msg createMessage(String text, int type, String channelId) {
        Msg msg = new Msg();
        msg.setSender(type);
        msg.setUsername(USER_NAME);
        msg.setTimestamp(System.currentTimeMillis());
        msg.setContent(text);
        msg.setChannelId(channelId);
        return msg;
    }

    public static void sendInfoMessages(ChatFragment chatFragment) {
        setWelcomeMsg(chatFragment);
        notifyWorkingHours(chatFragment);
        chatFragment.requestFullReload();
    }

    private static void notifyWorkingHours(ChatFragment chatFragment) {
        TimeZone timeZone = TimeZone.getTimeZone("GMT+5");
        Calendar calendar = Calendar.getInstance(timeZone);
        int hours = calendar.get(Calendar.HOUR_OF_DAY);
        if (hours >= 0 && hours <= 9) {
            sendLocalAgentMessage("Solve is at your disposal from 9:00am to 12:00 " +
                    "Midnight.Our dedicated team is working  to make it available for your service 24*7.", chatFragment);
        }
    }

    private static void setWelcomeMsg(ChatFragment chatFragment) {
        if (!MainApplication.data.loadBooleanData("showWelcomeMessage")) {
            Log.i(LOG_TAG, "setWelcomeMsg");
            MainApplication.data.saveData("showWelcomeMessage", true);
            sendLocalAgentMessage("Solve is your personal concierge app, we can cater " +
                    "to all your needs. Take a look at our services page for some examples of things " +
                    "we do. Is there anything I can help you with today?", chatFragment);
        }
    }

    private static void sendLocalAgentMessage(String text, ChatFragment chatFragment) {
        Msg message = createAgentMessage(text, ""); // dummy channel need to set it if using
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(message);
        realm.commitTransaction();
        chatFragment.requestFullReload();
    }
}
