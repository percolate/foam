package com.percolate.foam;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class DeleteFileCallback implements Callback<Object> {

    private final Context context;
    private final String storedExceptionFileName;

    DeleteFileCallback(Context context, String storedExceptionFileName) {
        this.context = context;
        this.storedExceptionFileName = storedExceptionFileName;
    }

    @Override
    public void success(Object o, Response response) {
        context.deleteFile(storedExceptionFileName);
    }

    @Override
    public void failure(RetrofitError error) {
        Utils.logIssue("RetrofitError", error);
    }
}
