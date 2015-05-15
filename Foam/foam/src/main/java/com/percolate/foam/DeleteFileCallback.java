package com.percolate.foam;

import android.content.Context;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Retrofit Callback.  Deletes a given file when network request is successful.
 */
class DeleteFileCallback implements Callback<Object> {

    private final Context context;
    private final String storedExceptionFileName;

    DeleteFileCallback(Context context, String storedExceptionFileName) {
        this.context = context;
        this.storedExceptionFileName = storedExceptionFileName;
    }

    /**
     * Request was successful.  Delete the file stored at <code>storedExceptionFileName</code>
     */
    @Override
    public void success(Object o, Response response) {
        try {
            context.deleteFile(storedExceptionFileName);
        } catch(Exception ex){
            new Utils().logIssue("Could not ", ex);
        }
    }

    /**
     * Request failed.  Log the RetrofitError.
     */
    @Override
    public void failure(RetrofitError error) {
        new Utils().logIssue("RetrofitError in DeleteFileCallback.  File: [" + storedExceptionFileName + "]", error);
    }
}
