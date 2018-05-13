package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 21.12.2015.
 */
public class OrderItem {

    @SerializedName("customer_id")
    private String customerId;
    @SerializedName("order_id")
    private int orderId;
    @SerializedName("item_id")
    private int itemId;
    private String quantity;
    private int amount;
    @SerializedName("created_at")
    private String createdAt;
    @SerializedName("mode_of_settlement")
    private String paymentType;
    @SerializedName("special")
    private String special;

    private String uom;
    @SerializedName("item_code")
    private String itemCode;

    public OrderItem(String customerId, int orderId, int itemId, int quantity) {
        this.orderId = orderId;
        this.itemId = itemId;
        this.quantity = String.valueOf(quantity);
        this.customerId = String.valueOf(customerId);
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public int getCustomerId() {
        try {
            return Integer.parseInt(customerId);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public void setCustomerId(int customerId) {
        this.customerId = String.valueOf(customerId);
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public int getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "amount=" + amount +
                ", quantity=" + quantity +
                ", itemId=" + itemId +
                ", orderId=" + orderId +
                ", customerId=" + customerId +
                '}';
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }
}
