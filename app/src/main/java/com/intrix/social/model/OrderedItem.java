package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/*
{"id":582,"customer_id":"84","item_id":"76","quantity":"1","order_id":274,"created_at":"2016-02-28T15:01:04.135Z","updated_at":"2016-02-28T15:01:04.135Z","item_code":null,"special":"testing dont make","uom":"POR"}]
*/

public class OrderedItem extends RealmObject {

    @PrimaryKey
    private int id;
    @SerializedName("order_id")
    private int orderId;
    private String quantity;
    @SerializedName("item_id")
    private int itemId;
    @SerializedName("item_code")
    private int itemCode;
    @SerializedName("customer_id")
    private String customerId;
    private String special;
    private String uom;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getItemCode() {
        return itemCode;
    }

    public void setItemCode(int itemCode) {
        this.itemCode = itemCode;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }
}
