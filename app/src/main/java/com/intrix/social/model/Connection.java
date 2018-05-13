package com.intrix.social.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class Connection extends RealmObject{

    @PrimaryKey
    private int id;
    private String poi_id;
    private int customer_id;
    private String location;
    private String status;
    private String chat_id;
    private String created_at;
    private String updated_at;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPoi_id() {
        return poi_id;
    }

    public void setPoi_id(String poi_id) {
        this.poi_id = poi_id;
    }

    public int getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getChat_id() {
        return chat_id;
    }

    public void setChat_id(String chat_id) {
        this.chat_id = chat_id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }
}