package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 27.01.2016.
 */
public class SettleRequest {

    @SerializedName("order_id")
    private int orderId;

    @SerializedName("settled_by")
    private String settledBy;

    public SettleRequest(int orderId, String settledBy) {
        this.orderId = orderId;
        this.settledBy = settledBy;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getSettledBy() {
        return settledBy;
    }

    public void setSettledBy(String settledBy) {
        this.settledBy = settledBy;
    }
}
