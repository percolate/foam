package com.percolate.foam;

import android.content.Context;
import android.util.Log;

import java.util.List;
import java.util.Map;

import retrofit.Callback;

/**
 * Foam's custom exception handler.
 *
 * Sets the application exception handler via <code>Thread.setDefaultUncaughtExceptionHandler()</code>.
 *
 * If an uncaught exception is triggered, we store the exception in application specific storage.
 * No app permissions are required since it uses internal storage.
 *
 * When the application is restarted, the stored exceptions will be processed (sent to any of the
 * available services that are configured to receive crash data).
 *
 * We can't send crash reports immediately, because that would mean doing network requests when
 * the app is trying to close from the crash.
 *
 * This object will not override other custom exception handlers.  It will chain with them.
 * Eg: if another crash reporting tool is configured, both custom exception handlers will run
 * in the reverse order they are initiated.
 */
class CustomExceptionHandler implements Thread.UncaughtExceptionHandler {

    private Context context;
    private Utils utils;

    /* Services that crashes will be reported to */
    private List<CrashReportingService> services;

    /* Only send events over WiFi */
    private boolean wifiOnly;

    /* Object used to store exceptions so they can be sent on next launch */
    private ExceptionPersister exceptionPersister;

    /* ExceptionHandler that was registered before we registered our version */
    private final Thread.UncaughtExceptionHandler defaultHandler;

    CustomExceptionHandler(Context context, List<CrashReportingService> crashReportingServices, boolean wifiOnly) {
        this.context = context;
        this.utils = new Utils();
        this.services = crashReportingServices;
        this.wifiOnly = wifiOnly;
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
     * the crash data.
     *
     * {@inheritDoc}
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        storeException(thread, ex);
        defaultHandler.uncaughtException(thread, ex);
    }

    /**
     * Create a {@link StoredException} object for the passed in Thread and Throwable.
     * Use our {@link ExceptionPersister} class to store this data on the device.
     *
     * @param thread Thread that generated the exception
     * @param ex Exception data.
     */
    private void storeException(Thread thread, Throwable ex) {
        String stackTrace = utils.trimToSize(Log.getStackTraceString(ex), 1024);

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
        if(!wifiOnly || utils.isOnWifi(context)) {
            for (Map.Entry<String, StoredException> entry : getStoredExceptions().entrySet()) {
                String fileName = entry.getKey();
                StoredException storedException = entry.getValue();

                for (CrashReportingService service : services) {
                    if (service.isEnabled()) {
                        Callback<Object> callback = new DeleteFileCallback(context, fileName);
                        service.logEvent(storedException, callback);
                    }
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
