package com.intrix.social.chat.networking;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.intrix.social.MainApplication;
import com.intrix.social.chat.model.Msg;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedString;

import static com.intrix.social.chat.networking.NetworkConfigs.ACTION_CHANNEL_CREATED;
import static com.intrix.social.chat.networking.NetworkConfigs.ACTION_PURPOSE_SET;
import static com.intrix.social.chat.networking.NetworkConfigs.CHANNELS_INVITE;
import static com.intrix.social.chat.networking.NetworkConfigs.CHANNEL_ID;
import static com.intrix.social.chat.networking.NetworkConfigs.CHAT_POST_MESSAGE;
import static com.intrix.social.chat.networking.NetworkConfigs.CREATE_CHANNEL;
import static com.intrix.social.chat.networking.NetworkConfigs.FILES_UPLOAD;
import static com.intrix.social.chat.networking.NetworkConfigs.SET_PURPOSE;
import static com.intrix.social.chat.networking.NetworkConfigs.SLACK_API_URL;
import static com.intrix.social.chat.networking.NetworkConfigs.SOCIAL_WALL;
import static com.intrix.social.chat.networking.NetworkConfigs.TOKEN;
import static com.intrix.social.chat.networking.NetworkConfigs.USER_ID;
import static com.intrix.social.chat.networking.NetworkConfigs.USER_NAME;
import static com.intrix.social.chat.networking.NetworkUtils.LOG_TAG;
import static com.intrix.social.chat.networking.NetworkUtils.readIs;
import static com.intrix.social.chat.networking.NetworkUtils.sendApiActionBroadcast;

/**
 * Created by yarolegovich on 8/4/15.
 */
public class Networker {

    private static final String TAG = Networker.class.getSimpleName();

    private SlackService mSlackService;

    private Realm mRealm;
    private Dispatcher mDispatcher;
    private ExecutorService mExecutorService;

    private Handler mMainHandler;

    private static Networker sInstance;

    public static Networker getInstance() {
        return sInstance != null ? sInstance : (sInstance = new Networker());
    }

    public static boolean realmUpdating = false;

    private Networker() {
        RestAdapter rest = new RestAdapter.Builder()
                .setEndpoint(SLACK_API_URL)
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {
                        request.addQueryParam("token", TOKEN);
                    }
                })
                .build();
        mSlackService = rest.create(SlackService.class);

        mDispatcher = new Dispatcher(this);
        mRealm = Realm.getDefaultInstance();
        mMainHandler = new Handler(Looper.getMainLooper());
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    /*
     * For thread safety dispatching commands using custom runnable class and executor service
     */
    private void execute(final String command, final String... params) {
        mDispatcher.setCommand(command);
        mDispatcher.setParams(params);
        mExecutorService.submit(mDispatcher);
    }

    public void sendMessage(String text) {
        sendMessage(CHANNEL_ID, text);
    }

    public void sendToWall(String text) {
        sendMessage(SOCIAL_WALL, text);
    }

    public void sendMessage(String channel, String text) {
        if (isOnUi()) {
            execute(CHAT_POST_MESSAGE, channel, text);
        } else {
            try {
                Response response = mSlackService.sendMessage(channel, USER_NAME, text);
                Log.d(TAG, readIs(response.getBody().in()));
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }
    public void reInitHandler()
    {
        mServerResponseHandler = new QueryMessagesHandler(CHANNEL_ID);
    }

    private QueryMessagesHandler mServerResponseHandler = new QueryMessagesHandler(CHANNEL_ID);
    private QueryMessagesHandler mWallResponseHandler = new QueryMessagesHandler(SOCIAL_WALL);

    private class QueryMessagesHandler implements Callback<Response>, Runnable {

        private String mChannelId;

        private QueryMessagesHandler(String channelId) {
            mChannelId = channelId;
        }

        @Override
        public void success(Response response, Response rawResponse) {
            try {
                String jsonString = readIs(rawResponse.getBody().in());
                JSONObject jsonResponse = new JSONObject(jsonString);
                Log.d(LOG_TAG,mChannelId +" " + jsonString);
                JSONArray jsonArray = jsonResponse.getJSONArray("messages");

                if (jsonArray.length() == 0) return;

                Log.d(TAG, "New messages queried, size: " + jsonArray.length());
                Log.i(TAG, " response " + jsonString);

                MessageBuilder messageBuilder = new MessageBuilder(mChannelId);

                List<Msg> messages = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    Msg msg = messageBuilder.buildModelObject(jsonArray.getJSONObject(i),
                            USER_NAME, USER_ID);
                    if (msg != null) {
                        messages.add(msg);
                    }
                }

                mRealm.beginTransaction();
                mRealm.copyToRealmOrUpdate(messages);
                mRealm.commitTransaction();
            } catch (Exception e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }

        @Override
        public void failure(RetrofitError error) {
            Log.e(TAG, error.getMessage(), error);
        }

        @Override
        public void run() {

            Realm mRealm = Realm.getDefaultInstance();

            RealmResults<Msg> latest = mRealm.where(Msg.class).equalTo("channelId",mChannelId)
                    .findAllSorted("timestamp", Sort.ASCENDING);
            String latestTimestamp = !latest.isEmpty() ? latest.first().getTimeString() : null;
            mSlackService.queryMessages(mChannelId, latestTimestamp, this);
        }
    }

    public void queryMessages() {
        mMainHandler.post(mServerResponseHandler);
    }

    public void queryWall() {
        mMainHandler.post(mWallResponseHandler);
    }

    public String uploadFile(String fileName) {
        if (isOnUi()) {
            execute(FILES_UPLOAD, fileName);
        } else {
            try {
                Response response = mSlackService.uploadFile(
                        new ProgressiveTypedFile("image/*", new File(fileName)),
                        new TypedString(CHANNEL_ID),
                        new TypedString(TOKEN));
                return readIs(response.getBody().in());
            } catch (Exception e) {
                Log.e(TAG, "Exception when sending file " + e.getMessage(), e);
            }
        }
        return "";
    }

    public void createChannel(String channelName) {
        if (isOnUi()) {
            execute(CREATE_CHANNEL, channelName);
        } else {
            try {
                Response response = mSlackService.createChannel(channelName);
                String responseString = readIs(response.getBody().in());

                Log.i(TAG, responseString);

                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getBoolean("ok")) {
                    JSONObject channelObj = jsonObject.getJSONObject("channel");

                    String id = channelObj.getString("id");
                    String creator = channelObj.getString("creator");
                    MainApplication.data.saveData("channel.id", id);
                    MainApplication.data.saveData("user.id", creator);

                    CHANNEL_ID = id;

//                    String webhookMessage = "A new user joined <#" + id + "|" + channelObj.getString("name") + ">";
//                    int status = sendRawPostRequest(WEBHOOKS + NEW_USER_HOOK, Arrays.asList
//                            (new Pair<>("text", webhookMessage), new Pair<>("username", USER_NAME)));
//                    responseString = "Status is" + status;
//                    Log.d(TAG, responseString);
//
//                    inviteMinions();

                    //EventBus.getDefault().post(new ChannelEvent("channelCreated", true, id));

                    sendApiActionBroadcast(ACTION_CHANNEL_CREATED, true, id);
                } else {
                    sendApiActionBroadcast(ACTION_CHANNEL_CREATED, false);
                }
            } catch (Exception e) {
                Log.e(TAG, "Exception when creating channel " + e.getMessage(), e);
                sendApiActionBroadcast(ACTION_CHANNEL_CREATED, false);
            }
        }
    }

    public void setPurpose() {
        if (isOnUi()) {
            execute(SET_PURPOSE);
        } else {
            try {
                String purpose = "Name: " + MainApplication.data.getUserName()
                        + " :: Number: " + MainApplication.data.getMobile()
                        + " :: LatLong: " + MainApplication.data.getLatLong()
                        + " :: Address: " + MainApplication.data.getAddress()
                        + " :: User_Typed_Address: " + MainApplication.data.getUserAddress()
                        + " :: email: " + MainApplication.data.loadData("user.email");

                Response response = mSlackService.setPurpose(CHANNEL_ID, purpose);
                String responseString = readIs(response.getBody().in());

                Log.d(TAG, responseString);

                JSONObject jsonObject = new JSONObject(responseString);

                if (jsonObject.getBoolean("ok")) {
                    MainApplication.data.saveData("PurposeSet", true);
                    sendApiActionBroadcast(ACTION_PURPOSE_SET, true);
                } else sendApiActionBroadcast(ACTION_PURPOSE_SET, false);

            } catch (Exception e) {
                Log.e(TAG, "Error when setting channel purpose " + e.getMessage(), e);
            }
        }
    }

    //Hardcoded as tv asked (until migration to mongo.db is in progress)
    private void inviteMinions() {
        try {
            String notificationsString = "";
            String[] ids = {"U08HCBEQ0", "U08GFPS69", "U0905770S"};
            String[] names = {"anand", "irfan", "navya"};
            for (int i = 0; i < ids.length; i++) {
                String userId = ids[i];
                String userName = names[i];
                notificationsString += "<@" + userId + "|" + userName + "> ";
                invite(userId);
            }
//            sendMessage(notificationsString);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public void invite() {
        invite("");
    }


    public void invite(String username) {
        if (isOnUi()) {
            execute(CHANNELS_INVITE, username);
        } else {
            try {
                Response response = mSlackService.invite(CHANNEL_ID, username);
                String responseString = readIs(response.getBody().in());

                Log.d(TAG, responseString);

                JSONObject jsonObject = new JSONObject(responseString);
                Log.d(TAG, jsonObject.toString());

            } catch (Exception e) {
                Log.e(TAG, "Error when inviting " + e.getMessage(), e);
            }
        }
    }

    private boolean isOnUi() {
        return Looper.getMainLooper().getThread() == Thread.currentThread();
    }
}
