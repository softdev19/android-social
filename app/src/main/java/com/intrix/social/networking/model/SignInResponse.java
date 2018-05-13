package com.intrix.social.networking.model;

import com.intrix.social.model.Errors;
import com.intrix.social.model.SignInData;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class SignInResponse {

    private String status;
    private Errors errors;
    private SignInData data;

    public boolean isSuccess() {
        return status.equals("success");
    }

    public String[] getErrors() {
        return errors.getMessages();
    }

    public SignInData getData() {
        return data;
    }
}

