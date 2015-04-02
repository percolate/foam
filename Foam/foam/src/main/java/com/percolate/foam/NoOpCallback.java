package com.percolate.foam;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class NoOpCallback implements Callback<Response> {

    @Override
    public void success(Response resp, Response response) {
    }

    @Override
    public void failure(RetrofitError error) {
    }
}
