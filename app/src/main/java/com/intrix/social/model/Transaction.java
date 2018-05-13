package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by sutharshan on 08.03.2016.
 *
 *
 * "id": 1,
 "customer_id": 1,
 "order_id": 2,
 "table_id": 89,
 "amount_settled": "441",
 "payment_mode": "cash",
 "status": null,
 "created_at": "2016-03-07T10:43:25.367Z",
 "updated_at": "2016-03-07T10:43:25.367Z",
 "rating": null,
 "comment": null,
 "location": null
 */

public class Transaction extends RealmObject {

    @PrimaryKey
    private int id;
    @SerializedName("customer_id")
    private int customerId;
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("table_id")
    private int tableId;
    @SerializedName("amount_settled")
    private String amount;
    @SerializedName("payment_mode")
    private String paymentType;
    private String status;
    private String rating;
    private String comment;
    private String location;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("updated_at")
    private String updatedAt;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }
}
