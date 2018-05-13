package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by yarolegovich on 15.01.2016.
 */
public class Split {

    int id;
    @SerializedName("no_of_people")
    private String noOfPeople;
    private int amount;
    private List<String> phones;
    @SerializedName("order_id")
    private int orderId;

    public String getNoOfPeople() {
        return noOfPeople;
    }

    public void setNoOfPeople(String noOfPeople) {
        this.noOfPeople = noOfPeople;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public List<String> getPhones() {
        return phones;
    }

    public void setPhones(List<String> phones) {
        this.phones = phones;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
