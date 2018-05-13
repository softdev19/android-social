package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

//{"id":3,"customer_id":84,"order_id":275,"table_id":85,"amount_settled":"100","payment_mode":"cash","status":null,"created_at":"2016-02-28T22:33:50.901Z","updated_at":"2016-02-28T22:33:50.901Z"}

public class SettlementResponse {

    private int id;
    @SerializedName("customer_id")
    private String customerId;
    @SerializedName("amount_settled")
    private String amountSettled;
    @SerializedName("table_id")
    private int tableId;
    @SerializedName("payment_mode")
    private String paymentMode;
    private String status;
    @SerializedName("order_id")
    private String orderId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getAmountSettled() {
        return amountSettled;
    }

    public void setAmountSettled(String amountSettled) {
        this.amountSettled = amountSettled;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
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

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
