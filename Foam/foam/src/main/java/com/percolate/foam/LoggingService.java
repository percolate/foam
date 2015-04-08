package com.percolate.foam;

import retrofit.Callback;

/**
 * Service that sends logs to a remote server.
 */
interface LoggingService extends Service {

    /**
     * Send data about the given exception.
     *
     * @param storedException Exception data to send.
     * @param callback Retrofit callback to execute after data is sent.
     */
    void logEvent(StoredException storedException, Callback<Object> callback);

    /**
     * Send single log file line.
     *
     * @param message Log message to send.
     */
    void logEvent(String message);

}
