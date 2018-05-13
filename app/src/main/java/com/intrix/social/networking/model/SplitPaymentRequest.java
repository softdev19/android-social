package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

public class SplitPaymentRequest {

    @SerializedName("split_payment")
    private SplitPayment splitPayment;
    public SplitPaymentRequest(SplitPayment splitPaymentObj) {
        this.splitPayment = splitPaymentObj;
    }
}
