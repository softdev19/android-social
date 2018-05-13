package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sutharsha on 28/02/16.
 */
public class Settlement {

    @SerializedName("customer_id")
    private int customerId;
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("table_id")
    private int tableId;
    @SerializedName("amount_settled")
    private String amountSettled;
    @SerializedName("payment_mode")
    private String paymentMode;
    @SerializedName("status")
    private String status;
    @SerializedName("settled_by")
    private String settledBy;


    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public String getAmountSettled() {
        return amountSettled;
    }

    public void setAmountSettled(String amountSettled) {
        this.amountSettled = amountSettled;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public void setPaymentMode(String paymentMode) {
        this.paymentMode = paymentMode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSettledBy() {
        return settledBy;
    }

    public void setSettledBy(String settledBy) {
        this.settledBy = settledBy;
    }
}
