package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by sutharsha on 08/01/16.
 */
public class App {

//    "category": "Productivity",
//            "rating": 4.5,
//            "updated": 1436741473,
//            "created": 1431028391,
//            "icon_url": "https://lh3.googleusercontent.com/q5pFGfXKZejowwcmlJl7M1IXGHVM4Zq_IjPpYb7zgkUFXO3QnZ2LyeOUUhMPaKPkJ3gR=w300",
//            "title": "WhereDat Beta",
//            "package": "com.nextstagesearch",
//            "ratings_count": 4,
//            "short_description": "WhereDat lets you easily search your device for contacts, apps, and recently accessed webpages without leaving your home screen. With deep linking you can search the c..."

    private String category;
    private String icon_url;
    private String title;
    @SerializedName("package")
    private String pack;
    private String shortdesc;
    private long updated;
    private long created;
    private long ratings_count;
    private float rating;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPack() {
        return pack;
    }

    public void setPack(String pack) {
        this.pack = pack;
    }

    public String getShortdesc() {
        return shortdesc;
    }

    public void setShortdesc(String shortdesc) {
        this.shortdesc = shortdesc;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getRatings_count() {
        return ratings_count;
    }

    public void setRatings_count(long ratings_count) {
        this.ratings_count = ratings_count;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }
}
