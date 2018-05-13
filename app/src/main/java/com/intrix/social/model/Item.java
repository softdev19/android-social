package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.Ignore;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class Item extends RealmObject{

    @PrimaryKey
    private int id;
    private String name;
    private String image;
    private String fav;
    private String description;
    private String price;
    @SerializedName("nooffriends")
    private String noOfFriends;
    @SerializedName("category_id")
    private int categoryId;
    @SerializedName("updated_at")
    private String updatedAt;
    private String uom;
    @SerializedName("pos_items_code")
    private String itemCode;

    @Ignore
    private String special;

    public String getUom() {
        return uom;
    }

    public void setUom(String uom) {
        this.uom = uom;
    }

    public String getItemCode() {
        return itemCode;
    }

    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }

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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFav() {
        return fav;
    }

    public void setFav(String fav) {
        this.fav = fav;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getNoOfFriends() {
        return noOfFriends;
    }

    public void setNoOfFriends(String noOfFriends) {
        this.noOfFriends = noOfFriends;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getSpecial() {
        return special;
    }

    public void setSpecial(String special) {
        this.special = special;
    }
}
