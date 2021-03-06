package com.percolate.foam;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Foam Main class.  Contains logic to initialize, start, and run Foam for an application.
 * This class should be started by {@link FoamApplicationInit}.
 *
 * To add new services to foam, modify {@link #init(FoamApiKeys)} to add your new service
 * to our <code>services</code> map, then modify {@link #initializeServices()} to pass the
 * correct API key to the service.  More details can be found in the README / wiki.
 */
class FoamMain {

    private Context context;
    
    /* Annotation on Application class containing service API Keys */
    FoamApiKeys foamApiKeys;

    /* Services that will receive events */
    Map<ServiceType, Service> services = new HashMap<>();

    /* Event tracking class.  Used to track all Activity entries (onResume) and custom events. */
    EventTracker eventTracker;

    /* Custom Exception handler class.  Used to capture, store, and send any uncaught exceptions. */
    CustomExceptionHandler customExceptionHandler;

    /* Class that monitors logcat output.  Error logs will be processed by appropriate services. */
    LogListener logListener;

    FoamMain(Context context){
        this.context = context;
    }

    /**
     * Add all services to <code>services</code> map, with an associated ServiceType enum.
     * @param foamApiKeys Instance of FoamApiKeys used to initialize foam services.
     */
    protected void init(FoamApiKeys foamApiKeys) {
        this.foamApiKeys = foamApiKeys;

        services.put(ServiceType.PAGERDUTY, new PagerDuty(context));
        services.put(ServiceType.HOCKEYAPP, new HockeyApp(context));
        services.put(ServiceType.PAPERTRAIL, new PaperTrail(context));
        services.put(ServiceType.LOGENTRIES, new LogEntries(context));
        services.put(ServiceType.MIXPANEL, new Mixpanel(context));
        services.put(ServiceType.FLURRY, new Flurry(context));
        services.put(ServiceType.GOOGLE_ANALYTICS, new GoogleAnalytics(context));
        services.put(ServiceType.GRAPHITE, new Graphite(context));
    }

    /**
     * Start up Foam!
     */
    void start(){
        initializeServices();
        startCustomExceptionHandler();
        startLogListener();
        startEventTracker();
    }

    /**
     * Disable foam
     */
    void stop() {
        if(eventTracker != null) {
            eventTracker.stop();
        }
        if(customExceptionHandler != null) {
            customExceptionHandler.stop();
        }
        if(logListener != null) {
            logListener.stop();
        }
    }

    /**
     * Loop through all services.  If there is a corresponding API key defined in FoamApiKeys,
     * then enable the service using that key.  Otherwise service will remain disabled.
     */
    void initializeServices() {
        for (Map.Entry<ServiceType, Service> entry : services.entrySet()) {
            ServiceType serviceType = entry.getKey();
            Service service = entry.getValue();

            String apiKey = null;
            if (serviceType == ServiceType.PAGERDUTY) {
                apiKey = foamApiKeys.pagerDuty();
            } else if (serviceType == ServiceType.HOCKEYAPP) {
                apiKey = foamApiKeys.hockeyApp();
            } else if (serviceType == ServiceType.PAPERTRAIL) {
                apiKey = foamApiKeys.papertrail();
            } else if (serviceType == ServiceType.LOGENTRIES) {
                apiKey = foamApiKeys.logentries();
            } else if (serviceType == ServiceType.MIXPANEL) {
                apiKey = foamApiKeys.mixpanel();
            } else if (serviceType == ServiceType.GOOGLE_ANALYTICS) {
                apiKey = foamApiKeys.googleAnalytics();
            } else if (serviceType == ServiceType.FLURRY) {
                if (((Flurry) service).checkForJar()) {
                    apiKey = foamApiKeys.flurry();
                }
            } else if(serviceType == ServiceType.GRAPHITE){
                apiKey = foamApiKeys.graphite();
            }

            if(new Utils().isNotBlank(apiKey)){
                service.enable(apiKey);
            }

        }
    }

    /**
     * Start Foam custom exception handler class.
     * @see CustomExceptionHandler
     */
    void startCustomExceptionHandler() {
        List<CrashReportingService> services = getEnabledServicesForType(CrashReportingService.class);
        if(services != null && !services.isEmpty()) {
            if(customExceptionHandler == null) {
                customExceptionHandler = new CustomExceptionHandler(context, services, foamApiKeys.wifiOnly());
            }
            if(!customExceptionHandler.isRunning()) {
                customExceptionHandler.start();
            }
        }
    }

    /**
     * Start Foam log listener class.
     * @see LogListener
     */
    void startLogListener() {
        List<LoggingService> services = getEnabledServicesForType(LoggingService.class);
        if(services != null && !services.isEmpty()) {
            if(logListener == null) {
                logListener = new LogListener(context, services, foamApiKeys.wifiOnly());
            }
            if(!logListener.isRunning()) {
                logListener.start();
            }
        }
    }

    /**
     * Start Foam event tracker.  Checks for duplicate restarts.
     * @see EventTracker
     */
    void startEventTracker(){
        List<EventTrackingService> services = getEnabledServicesForType(EventTrackingService.class);
        if(services != null && !services.isEmpty()) {
            if(eventTracker == null) {
                eventTracker = new EventTracker(context, services, foamApiKeys.wifiOnly());
            }
            if(!eventTracker.isRunning()) {
                eventTracker.start();
            }
        }
    }

    /**
     * Return Service classes defined in {@link #services} that match the given interface (one of
     * Service's child interfaces).
     *
     * @param <T> Type for the option value
     * @param clazz Interface (child of {@link Service} interface).  The returned <code>List</code>
     *              will be all Service's from our <code>services</code> map that can be cast to this
     *              interface type.
     * @return List of type T.  This is a list of all Services that are of the pass in interface type.
     *
     */
    @SuppressWarnings("unchecked")
    <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
        List<T> servicesOfType = new ArrayList<>();
        if(services != null && !services.isEmpty()) {
            for (Service service : services.values()) {
                if (clazz.isAssignableFrom(service.getClass())) {
                    if (service.isEnabled()) {
                        servicesOfType.add((T) service);
                    }
                }
            }
        }
        return servicesOfType;
    }

    /**
     * Used by {@link com.percolate.foam.FoamEvent} to log custom events.
     * @param context Context
     * @param event Event name to track.
     */
    void logEvent(Context context, String event){
        if(eventTracker != null) {
            eventTracker.trackEvent(context, event);
        }
    }

}
