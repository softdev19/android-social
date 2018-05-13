package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 27.01.2016.
 */
public class TablesResponse {

    private int id;
    @SerializedName("TableId")
    private int tableId;
    @SerializedName("TableCode")
    private int tableCode;
    @SerializedName("TableCapacity")
    private int tableCapacity;
    @SerializedName("TableLocationCode")
    private int tableLocationCode;
    @SerializedName("FloorLocationCode")
    private String floorLocation;
    @SerializedName("TableRecordStatus")
    private String tableRecordStatus;
    private String status;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getTableId() {
        return tableId;
    }

    public void setTableId(int tableId) {
        this.tableId = tableId;
    }

    public int getTableCode() {
        return tableCode;
    }

    public void setTableCode(int tableCode) {
        this.tableCode = tableCode;
    }

    public int getTableCapacity() {
        return tableCapacity;
    }

    public void setTableCapacity(int tableCapacity) {
        this.tableCapacity = tableCapacity;
    }

    public int getTableLocationCode() {
        return tableLocationCode;
    }

    public void setTableLocationCode(int tableLocationCode) {
        this.tableLocationCode = tableLocationCode;
    }

    public String getFloorLocation() {
        return floorLocation;
    }

    public void setFloorLocation(String floorLocation) {
        this.floorLocation = floorLocation;
    }

    public String getTableRecordStatus() {
        return tableRecordStatus;
    }

    public void setTableRecordStatus(String tableRecordStatus) {
        this.tableRecordStatus = tableRecordStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
