package com.percolate.foam;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Retrofit Callback.  Deletes a given file when network request is successful.
 */
class DeleteFileCallback implements Callback<Object> {

    final Context context;
    final String storedExceptionFileName;
    Utils utils;

    DeleteFileCallback(Context context, String storedExceptionFileName) {
        this.context = context;
        this.storedExceptionFileName = storedExceptionFileName;
        this.utils = new Utils();
    }

    /**
     * Request was successful.  Delete the file stored at <code>storedExceptionFileName</code>
     */
    @Override
    public void success(Object o, Response response) {
        try {
            context.deleteFile(storedExceptionFileName);
        } catch(Exception ex){
            utils.logIssue("Could not delete file [" + storedExceptionFileName + "]", ex);
        }
    }

    /**
     * Request failed.  Log the RetrofitError.
     */
    @Override
    public void failure(RetrofitError error) {
        utils.logIssue("RetrofitError in DeleteFileCallback.  File: [" + storedExceptionFileName + "]", error);
    }
}
