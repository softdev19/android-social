package com.intrix.social.networking.model;

/**
 * Created by yarolegovich on 04.01.2016.
 */
public class FavoriteRequest {

    private Item item;

    public FavoriteRequest(String fav) {
        item = new Item(fav);
    }
}

class Item {
    private String fav;

    Item(String fav) {
        this.fav = fav;
    }
}
