package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class CustomerMini {

    private Integer id;
    private String name;
    private String location;
    private String mobileno;
    private String pic;
    private String interest1;
    private String interest2;
    private String interest3;
    private String description;
    @SerializedName("who_are_you")
    private String description2;
    @SerializedName("what_are_you_looking_for")
    private String description3;
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
    @SerializedName("gcm_id")
    private String gcmToken;
    private Integer amount;
    private String city;
    private int rank;
    private Double latitude;
    private Double longitude;
    private boolean insocial;

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

    public String getGcmToken() {
        return gcmToken;
    }

    public void setGcmToken(String gcmToken) {
        this.gcmToken = gcmToken;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getDescription2() {
        return description2;
    }

    public void setDescription2(String description2) {
        this.description2 = description2;
    }

    public String getDescription3() {
        return description3;
    }

    public void setDescription3(String description3) {
        this.description3 = description3;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isInsocial() {
        return insocial;
    }

    public void setInsocial(boolean insocial) {
        this.insocial = insocial;
    }

    public CustomerMini(Customer cust)
    {
        setId(cust.getId());
        setName(cust.getName());
        setPic(cust.getPic());
        setDescription(cust.getDescription());
        setDescription2(cust.getDescription2());
        setDescription3(cust.getDescription3());
        setEmail(cust.getEmail());
        setInterest1(cust.getInterest1());
        setInterest2(cust.getInterest2());
        setInterest3(cust.getInterest3());
        setLatitude(cust.getLatitude());
        setLongitude(cust.getLongitude());
        setLocation(cust.getLocation());
        setCity(cust.getCity());
    }

    public CustomerMini()
    {
    }
}
