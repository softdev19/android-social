package com.intrix.social.networking;

import com.intrix.social.model.GetResult;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by yarolegovich on 23.11.2015.
 */
public class GetCallbackWrapper<T> implements Callback<GetResult<T>> {

    private Callback<List<T>> mWrapped;

    public GetCallbackWrapper(Callback<List<T>> wrapped) {
        mWrapped = wrapped;
    }

    @Override
    public void success(GetResult<T> tGetResult, Response response) {
        mWrapped.success(tGetResult.getItems(), response);
    }

    @Override
    public void failure(RetrofitError error) {
        mWrapped.failure(error);
    }
}
