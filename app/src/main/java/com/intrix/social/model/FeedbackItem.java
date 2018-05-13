package com.intrix.social.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 23.12.2015.
 */
public class FeedbackItem {
    private int id;
    private String ratings;
    @SerializedName("item_id")
    private int itemId;
    @SerializedName("feedback_id")
    private int feedBackId;
    private String comment;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getRatings() {
        return ratings;
    }

    public void setRatings(String ratings) {
        this.ratings = ratings;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getFeedBackId() {
        return feedBackId;
    }

    public void setFeedBackId(int feedBackId) {
        this.feedBackId = feedBackId;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
}
