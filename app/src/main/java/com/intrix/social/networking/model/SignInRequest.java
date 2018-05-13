package com.intrix.social.networking.model;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class SignInRequest {

    public final String email;
    public final String password;

    public SignInRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
