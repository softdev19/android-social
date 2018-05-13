package com.intrix.social.networking.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 *This is a temp object used to store other customers.Gets refreshed when new data comes in.
 *
 *
 */
public class OtherCustomer extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String location;
    private String mobileno;
    private String pic;
    private String interest1;
    private String interest2;
    private String interest3;
    private String description;
    private String fblink;
    private String twtlink;
    private String belink;
    private String drlink;
    private String sclink;
    private String otherlink;
    private String otherinterest;
    private String email;
    private String upvotes;
    private String downvotes;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }

    public String getMobileno() {
        return mobileno;
    }

    public void setMobileno(String mobileno) {
        this.mobileno = mobileno;
    }

    public String getPic() {
        return pic;
    }

    public void setPic(String pic) {
        this.pic = pic;
    }

    public String getInterest1() {
        return interest1;
    }

    public void setInterest1(String interest1) {
        this.interest1 = interest1;
    }

    public String getInterest2() {
        return interest2;
    }

    public void setInterest2(String interest2) {
        this.interest2 = interest2;
    }

    public String getInterest3() {
        return interest3;
    }

    public void setInterest3(String interest3) {
        this.interest3 = interest3;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFblink() {
        return fblink;
    }

    public void setFblink(String fblink) {
        this.fblink = fblink;
    }

    public String getTwtlink() {
        return twtlink;
    }

    public void setTwtlink(String twtlink) {
        this.twtlink = twtlink;
    }

    public String getBelink() {
        return belink;
    }

    public void setBelink(String belink) {
        this.belink = belink;
    }

    public String getDrlink() {
        return drlink;
    }

    public void setDrlink(String drlink) {
        this.drlink = drlink;
    }

    public String getSclink() {
        return sclink;
    }

    public void setSclink(String sclink) {
        this.sclink = sclink;
    }

    public String getOtherlink() {
        return otherlink;
    }

    public void setOtherlink(String otherlink) {
        this.otherlink = otherlink;
    }

    public String getOtherinterest() {
        return otherinterest;
    }

    public void setOtherinterest(String otherinterest) {
        this.otherinterest = otherinterest;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(String upvotes) {
        this.upvotes = upvotes;
    }

    public String getDownvotes() {
        return downvotes;
    }

    public void setDownvotes(String downvotes) {
        this.downvotes = downvotes;
    }
}
