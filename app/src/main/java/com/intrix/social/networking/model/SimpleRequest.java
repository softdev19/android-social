package com.intrix.social.networking.model;

/**
 * Created by yarolegovich on 22.12.2015.
 */
public class SimpleRequest {
    private int id;

    public SimpleRequest(int userId)
    {
        id = userId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
