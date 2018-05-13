package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppsResponse {

    @SerializedName("apps")
    private List<App> apps;
    public AppsResponse(List<App> apps) {
        this.apps = apps;
    }

    public List<App> getApps() {
        return apps;
    }
}
