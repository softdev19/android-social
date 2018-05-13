package com.intrix.social.model;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class SignInData {
    private String id;
    private String uid;
    private String email;

    public String getId() {
        return id;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "SignInData{" +
                "email='" + email + '\'' +
                ", uid='" + uid + '\'' +
                ", id='" + id + '\'' +
                '}';
    }
}
