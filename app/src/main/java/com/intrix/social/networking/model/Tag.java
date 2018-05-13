package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sutharsha on 19/02/16.
 */
public class Tag {
    /*
    {
  "id": 2,
  "customer_id": 2,
  "order_id": 1,
  "pos_order_id": null,
  "customer_name": null,
  "image_url": null,
  "created_at": "2016-02-18T19:16:08.425Z",
  "updated_at": "2016-02-18T19:16:08.425Z",
  "setteled": null
}
     */

    private int id;
    @SerializedName("customer_id")
    private int customerId;
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("pos_order_id")
    private int posOrderId;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("image_url")
    private String imageUrl;
    @SerializedName("setteled")
    private boolean settled;
    @SerializedName("tagged_by")
    private int taggerId;



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

    public int getPosOrderId() {
        return posOrderId;
    }

    public void setPosOrderId(int posOrderId) {
        this.posOrderId = posOrderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public boolean isSettled() {
        return settled;
    }

    public void setSettled(boolean settled) {
        this.settled = settled;
    }

    public int getTaggerId() {
        return taggerId;
    }

    public void setTaggerId(int taggerId) {
        this.taggerId = taggerId;
    }
}
