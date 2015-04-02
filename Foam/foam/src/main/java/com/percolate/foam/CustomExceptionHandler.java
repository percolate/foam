package com.percolate.foam;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Map;

import retrofit.Callback;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 *
 * Custom exception handler.
 *
 * Sets the application exception handler via <code>Thread.setDefaultUncaughtExceptionHandler()</code>.
 *
 * If an uncaught exception is triggered, we store the exception in application specific storage.
 * No app permissions are required since it uses internal storage.
 *
 * When the application is restarted, the stored exceptions will be processed (sent to any of the
 * available services that are configured to receve crash data).
 *
 * We can't send crash reports immediatly, because that would mean doing network requests when
 * the app is trying to close from the crash.
 *
 * This object will not override other custom exception handlers.  It will chain with them.
 * Eg: if another crash reporting tool is configured, both custom exception handlers will run
 * in the reverse order they are initiated.
 */
class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;
    private List<CrashReportingService> services;
    private ExceptionPersister exceptionPersister;
    private final Thread.UncaughtExceptionHandler defaultHandler;

    CustomExceptionHandler(Context context, List<CrashReportingService> crashReportingServices) {
        this.context = context;
        this.services = crashReportingServices;
        this.defaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.exceptionPersister = new ExceptionPersister(context);
    }

    /**
     * Change application exception handler with our own (this class).
     * Send any stored exceptions that were caught last time.
     */
    public void start() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        sendStoredExceptions();
    }

    /**
     * Handle uncaught exceptions.  Thread.UncaughtExceptionHandler interface method.
     * Here, for each service that the user has enabled, we will store a local copy of
     * all of the crash data.
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        storeException(thread, ex);
        defaultHandler.uncaughtException(thread, ex);
    }

    private void storeException(Thread thread, Throwable ex) {
        String stackTrace = Utils.trimToSize(Log.getStackTraceString(ex), 1024);

        for (Service service : services) {
            if(service.isEnabled()){
                exceptionPersister.store(new StoredException(
                        service.getServiceType(),
                        ex.getMessage(),
                        thread.getName(),
                        stackTrace
                ));
            }
        }
    }

    /**
     * Iterate through any stored exception files that have not been successfully sent.  Send
     * them to their corresponding service.
     */
    public void sendStoredExceptions(){
        for (Map.Entry<String, StoredException> entry : getStoredExceptions().entrySet()) {
            String fileName = entry.getKey();
            StoredException storedException = entry.getValue();

            for (CrashReportingService service : services) {
                if(service.isEnabled()) {
                    Callback<Object> callback = new DeleteFileCallback(context, fileName);
                    service.logEvent(storedException, callback);
                }
            }
        }
    }

    /**
     * Get all stored exceptions.
     */
    private Map<String, StoredException> getStoredExceptions(){
        return exceptionPersister.loadAll();
    }

}
