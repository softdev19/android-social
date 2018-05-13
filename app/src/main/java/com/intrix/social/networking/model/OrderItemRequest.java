package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;
import com.intrix.social.model.OrderItem;

/**
 * Created by yarolegovich on 21.12.2015.
 */
public class OrderItemRequest {
    @SerializedName("ordereditem")
    private OrderItem orderItem;

    public OrderItemRequest(OrderItem orderItem) {
        this.orderItem = orderItem;
    }
}
