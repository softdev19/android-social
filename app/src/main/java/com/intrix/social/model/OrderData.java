package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 21.12.2015.
 */
public class OrderData {

    @SerializedName("pos_order_id")
    private int posOrderId;
    @SerializedName("table_no")
    private int tableNo;
    @SerializedName("customer_id")
    private String customerId;


    public OrderData(int posOrderId, int tableNo, int customerId) {
        this.posOrderId = posOrderId;
        this.tableNo = tableNo;
        this.customerId = ""+customerId;
    }
}
