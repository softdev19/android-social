package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class Feedback {

    private int id;
    private int rating;
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("comments")
    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getComments() {
        return comment;
    }
    
    public void setComments(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "Feedback{" +
                "rating=" + rating +
                ", orderId=" + orderId +
                ", comments='" + comment + '\'' +
                '}';
    }
}
