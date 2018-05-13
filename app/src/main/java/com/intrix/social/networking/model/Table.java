package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 27.01.2016.
 */
public class Table {

//    @SerializedName("TableCode")
//    private int tableCode;
    @SerializedName("TableId")
    private Integer tableNo;
    @SerializedName("customer_id")
    private Integer customerId;
    private String status;

    public Table(int tableNo, int customerID) {
        this.tableNo = tableNo;
        this.customerId = customerID;
    }

    public Table(int tableNo, int customerID, String status) {
        this.tableNo = tableNo;
        this.customerId = customerID;
        this.status = status;
    }

    public Integer getTableNo() {
        return tableNo;
    }

    public void setTableNo(Integer tableNo) {
        this.tableNo = tableNo;
    }

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
