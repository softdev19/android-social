package com.intrix.social.networking.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Chat extends RealmObject{

    @PrimaryKey
    private int id;
    private String name;
    private String person_1_id;
    private String person_2_id;
    private String location;
    private String table_no;
    private String offline;
    private String feedback;
    private String created_at;
    private String updated_at;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPerson_1_id() {
        return person_1_id;
    }

    public void setPerson_1_id(String person_1_id) {
        this.person_1_id = person_1_id;
    }

    public String getPerson_2_id() {
        return person_2_id;
    }

    public void setPerson_2_id(String person_2_id) {
        this.person_2_id = person_2_id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTable_no() {
        return table_no;
    }

    public void setTable_no(String table_no) {
        this.table_no = table_no;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOffline() {
        return offline;
    }

    public void setOffline(String offline) {
        this.offline = offline;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
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