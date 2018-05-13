package com.intrix.social.networking.model;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class ChatRQ {

    String name;
    String person_1_id;
    String person_2_id;
    String location;
    String table_no;

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
}