package com.percolate.foam;

import java.io.Serializable;

/**
 * DTO style object that will be persisted to/from local storage.
 */
class StoredException implements Serializable {

    private static final long serialVersionUID = -890541737111675209L;

    protected ServiceType platform;
    protected String message;
    protected String threadName;
    protected String stackTrace;

    @SuppressWarnings("unused")
    StoredException(){
    }

    StoredException(ServiceType platform, String message, String threadName, String stackTrace) {
        this.platform = platform;
        this.message = message;
        this.threadName = threadName;
        this.stackTrace = stackTrace;
    }

}
