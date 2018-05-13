package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AppsRequest {

    @SerializedName("packages")
    private List<String> packages;
    public AppsRequest(List<String> packages) {
        this.packages = packages;
    }
}
