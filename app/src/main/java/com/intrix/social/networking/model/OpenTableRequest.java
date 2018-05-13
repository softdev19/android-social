package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 27.01.2016.
 */
public class OpenTableRequest {

    @SerializedName("table_code")
    private int tableCode;
    @SerializedName("no_of_users")
    private int noOfUsers;
    @SerializedName("customer_id")
    private int customerId;

    public OpenTableRequest(int tableCode, int noOfUsers, int customerID) {
        this.tableCode = tableCode;
        this.noOfUsers = noOfUsers;
        this.customerId = customerID;
    }

    public OpenTableRequest(int tableCode, int noOfUsers) {
        this.tableCode = tableCode;
        this.noOfUsers = noOfUsers;
    }

    public int getTableCode() {
        return tableCode;
    }

    public void setTableCode(int tableCode) {
        this.tableCode = tableCode;
    }

    public int getNoOfUsers() {
        return noOfUsers;
    }

    public void setNoOfUsers(int noOfUsers) {
        this.noOfUsers = noOfUsers;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
}
