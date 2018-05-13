package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class Category extends RealmObject {

    @PrimaryKey
    private int id;
    private String name;
    private String favorite;
    private String comment;
    private String image;
    @SerializedName("cattype")
    private String catType;
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

    public String getFavorite() {
        return favorite;
    }

    public void setFavorite(String favorite) {
        this.favorite = favorite;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCatType() {
        return catType;
    }

    public void setCatType(String catType) {
        this.catType = catType;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }


}
