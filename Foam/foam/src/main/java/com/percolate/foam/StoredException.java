package com.percolate.foam;

import java.io.Serializable;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class StoredException implements Serializable {

    private static final long serialVersionUID = -890541737111675209L;

    protected ServiceType platform;
    protected String message;
    protected String threadName;
    protected String stackTrace;

    StoredException(){
    }

    StoredException(ServiceType platform, String message, String threadName, String stackTrace) {
        this.platform = platform;
        this.message = message;
        this.threadName = threadName;
        this.stackTrace = stackTrace;
    }

}
