package com.intrix.social.networking;

import com.intrix.social.networking.model.InstaMojoRequest;
import com.intrix.social.networking.model.PaymentResponse;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Headers;
import retrofit.http.POST;

import static com.intrix.social.networking.NetworkConfigs.PAYMENTS;

/**
 * Created by yarolegovich on 8/9/15.
 */
public interface PaymentService {

    @POST(PAYMENTS)
    @Headers({
            "X-Api-Key: 50b5bc3c6ac0f96b6bdd8e5e5b449756",
            "X-Auth-Token: 2e448f78b98193441b07b5f3f83127e7",
            "Content-Type: application/json"
    })
    void processOnlinePayment(@Body InstaMojoRequest appsRequest, Callback<PaymentResponse> callback);
}
