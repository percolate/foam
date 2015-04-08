package com.percolate.foam;

import retrofit.Callback;

/**
 * Service that supports receiving crash data.
 */
interface CrashReportingService extends Service {

    /**
     * Send data from passed in {@see StoredException} object to backing service.
     * @param storedException StoredException data.  Never null.
     * @param callback Retrofit callback.  Use {@link com.percolate.foam.NoOpCallback} if no
     *                 action is required.
     */
    void logEvent(StoredException storedException, Callback<Object> callback);

}
