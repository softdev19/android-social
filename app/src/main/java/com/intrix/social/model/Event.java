package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class Event extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String location;
    private String poster;
    @SerializedName("eventdate")
    private String eventDate;
    @SerializedName("eventtime")
    private String eventTime;
    private String artist;
    @SerializedName("artist_description")
    private String artistDescription;
    private String category;
    @SerializedName("no_of_people")
    private String noOfPeople;
    private String amount;
    @SerializedName("updated_at")
    private String updatedAt;

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

    public String getPoster() {
        return poster;
    }

    public void setPoster(String poster) {
        this.poster = poster;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistDescription() {
        return artistDescription;
    }

    public void setArtistDescription(String artistDescription) {
        this.artistDescription = artistDescription;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getNoOfPeople() {
        return noOfPeople;
    }

    public void setNoOfPeople(String noOfPeople) {
        this.noOfPeople = noOfPeople;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}
