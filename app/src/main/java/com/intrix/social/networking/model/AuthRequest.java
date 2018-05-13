package com.intrix.social.networking.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class AuthRequest {

    public final String email;
    public final String password;
    @SerializedName("password_confirmation")
    public final String passwordConfirmation;

    public AuthRequest(String email, String password, String passwordConfirmation) {
        this.email = email;
        this.password = password;
        this.passwordConfirmation = passwordConfirmation;
    }
}
