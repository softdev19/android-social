package com.intrix.social.model;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class OutletResponse {


    @SerializedName("id")
    int id;

    @SerializedName("name")
    private String name;

    @SerializedName("ratings")
    private String ratings;

    @SerializedName("reviews")
    private String reviews;

    @SerializedName("checked_in")
    private String checked_in;

    @SerializedName("image")
    private String image;

    @SerializedName("phone_number")
    private String phone_number;

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


    public String getRatings() {


        return ratings;
    }

    public void setRating(String ratings) {
        this.ratings = ratings;
    }


    public String getReviews() {


        return reviews;
    }

    public void setReviews(String reviews) {
        this.reviews = reviews;
    }


    public String getCheckedIn() {


        return checked_in;
    }

    public void setCheckedIn(String checked_in) {
        this.checked_in = checked_in;
    }


    public String getImage() {


        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }


    public String getPhoneNumber() {


        return phone_number;
    }

    public void setPhoneNumber(String phone_number) {
        this.phone_number = phone_number;
    }


}
