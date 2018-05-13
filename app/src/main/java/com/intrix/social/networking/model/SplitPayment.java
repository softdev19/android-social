package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

public class SplitPayment {

    private Integer id;
    @SerializedName("split_id")
    private int splitId;
    @SerializedName("customer_id")
    private int customerId;
    private int amount;
    private String mode;

    public int getSplitId() {
        return splitId;
    }

    public void setSplitId(int splitId) {
        this.splitId = splitId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
