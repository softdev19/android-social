package com.intrix.social.chat.abstractions;

import org.json.JSONObject;

/**
 * Created by yarolegovich on 8/4/15.
 */
public interface ModelBuilder<T, U> {
    T buildModelObject(JSONObject jsonObject, U... params);
}
