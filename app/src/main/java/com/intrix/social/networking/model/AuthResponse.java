package com.intrix.social.networking.model;

import com.intrix.social.model.AuthData;
import com.intrix.social.model.Errors;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class AuthResponse {

    private String status;
    private Errors errors;
    private AuthData data;

    public boolean isSuccess() {
        return status.equals("success");
    }

    public String[] getErrors() {
        return errors.getMessages();
    }

    public AuthData getData() {
        return data;
    }
}

