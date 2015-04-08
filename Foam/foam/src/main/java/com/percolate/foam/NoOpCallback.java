package com.percolate.foam;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Retrofit Callback object that does nothing with response data.
 */
class NoOpCallback implements Callback<Response> {

    @Override
    public void success(Response resp, Response response) {
    }

    @Override
    public void failure(RetrofitError error) {
    }
}
