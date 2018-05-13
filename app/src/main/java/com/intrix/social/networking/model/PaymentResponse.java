package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sutharsha on 29/02/16.
 */
public class PaymentResponse {

    @SerializedName("payment_request")
    private InstaMojoRequest paymentRequest;
    private boolean success;

    public InstaMojoRequest getPaymentRequest() {
        return paymentRequest;
    }

    public void setPaymentRequest(InstaMojoRequest paymentRequest) {
        this.paymentRequest = paymentRequest;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }
}
