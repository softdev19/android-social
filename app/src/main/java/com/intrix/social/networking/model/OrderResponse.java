package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 21.12.2015.
 */
public class OrderResponse {

    private int id;
    @SerializedName("customer_name")
    private String customerName;
    @SerializedName("phone_no")
    private String phoneNo;
    @SerializedName("table_no")
    private int tableNo;
    private String split;
    private String amount;
    private String settled;
    @SerializedName("mode_of_settlement")
    private String modeOfSettlement;
    @SerializedName("bill_id")
    private String billId;
    @SerializedName("pod_order_id")
    private String posOrderId;
    @SerializedName("waiter_name")
    private String waiterName;
    @SerializedName("customer_id")
    private String customerId;

    public int getId() {
        return id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getPhoneNo() {
        return phoneNo;
    }

    public int getTableNo() {
        return tableNo;
    }

    public String getSplit() {
        return split;
    }

    public String getAmount() {
        return amount;
    }

    public String getSettled() {
        return settled;
    }

    public String getModeOfSettlement() {
        return modeOfSettlement;
    }

    public String getBillId() {
        return billId;
    }

    public String getPosOrderId() {
        return posOrderId;
    }

    public String getWaiterName() {
        return waiterName;
    }

    public String getCustomerId() {
        return customerId;
    }
}
