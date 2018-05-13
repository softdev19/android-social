package com.intrix.social.chat.networking;

import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Query;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;

import static com.intrix.social.chat.networking.NetworkConfigs.CHANNELS_HISTORY;
import static com.intrix.social.chat.networking.NetworkConfigs.CHANNELS_INVITE;
import static com.intrix.social.chat.networking.NetworkConfigs.CHAT_POST_MESSAGE;
import static com.intrix.social.chat.networking.NetworkConfigs.CREATE_CHANNEL;
import static com.intrix.social.chat.networking.NetworkConfigs.FILES_UPLOAD;
import static com.intrix.social.chat.networking.NetworkConfigs.SET_PURPOSE;

/**
 * Created by yarolegovich on 8/4/15.
 */
public interface SlackService {

    @GET(CHANNELS_HISTORY)
    void queryMessages(@Query("channel") String channel,
                       @Query("oldest") String timestamp,
                       Callback<Response> callback);
    

    @GET(CHAT_POST_MESSAGE)
    Response sendMessage(@Query("channel") String channel,
                         @Query("username") String username,
                         @Query("text") String text);

    @GET(CREATE_CHANNEL)
    Response createChannel(@Query("name") String channelName);

    @GET(SET_PURPOSE)
    Response setPurpose(@Query("channel") String channel,
                        @Query("purpose") String purpose);

    @GET(CHANNELS_INVITE)
    Response invite(@Query("channel") String channel,
                    @Query("user") String user);

    @Multipart
    @POST(FILES_UPLOAD)
    Response uploadFile(@Part("file") TypedFile photo,
                        @Part("channels") TypedString channels,
                        @Part("token") TypedString token);
}
