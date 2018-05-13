package com.intrix.social.networking;

import com.intrix.social.networking.model.App;
import com.intrix.social.networking.model.AppsRequest;
import com.intrix.social.networking.model.AppsResponse;

import java.util.List;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.POST;

import static com.intrix.social.networking.NetworkConfigs.APPS;

/**
 * Created by yarolegovich on 8/9/15.
 */
public interface AppsService {

    @POST(APPS)
    void getApps(@Body AppsRequest appsRequest, Callback<AppsResponse> callback);
}
