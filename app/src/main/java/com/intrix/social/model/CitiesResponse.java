package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

public class CitiesResponse {

    @SerializedName("id")
    int id;

    @SerializedName("name")
    private String name;

    @SerializedName("location")
    private String location;

    @SerializedName("lat")
    private String lat;

    @SerializedName("long")
    private String lon;

    @SerializedName("created_at")
    private String created_at;

    @SerializedName("updated_at")
    private String updated_at;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {


        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLocation() {


        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    public String getLat() {


        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }


    public String getLong() {


        return lon;
    }

    public void setLong(String lon) {
        this.lon = lon;
    }


    public String getCreatedAt() {


        return created_at;
    }

    public void setCreatedAt(String created_at) {
        this.created_at = created_at;
    }


    public String getUpdatedAt() {


        return updated_at;
    }

    public void setUpdatedAt(String updated_at) {
        this.updated_at = updated_at;
    }


}
