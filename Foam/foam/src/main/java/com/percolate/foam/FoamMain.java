package com.percolate.foam;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class FoamMain {

    private Context context;
    private FoamApiKeys foamApiKeys;

    private Map<ServiceType, Service> services = new HashMap<>();

    private EventTracker eventTracker;

    FoamMain(Context context){
        this.context = context;
    }

    protected void init(FoamApiKeys foamApiKeys) {
        this.foamApiKeys = foamApiKeys;

        services.put(ServiceType.PAGERDUTY, new PagerDuty(context));
        services.put(ServiceType.HOCKEYAPP, new HockeyApp(context));
        services.put(ServiceType.PAPERTRAIL, new PaperTrail(context));
        services.put(ServiceType.LOGENTRIES, new LogEntries(context));
        services.put(ServiceType.MIXPANEL, new Mixpanel(context));
        services.put(ServiceType.FLURRY, new Flurry(context));
        services.put(ServiceType.GOOGLE_ANALYTICS, new GoogleAnalytics(context));
    }

    protected void start(){
        initializeServices();
        startCustomExceptionHandler();
        startLogListener();
        startEventTracker();
    }

    private void initializeServices() {
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
            }

            if(Utils.isNotBlank(apiKey)){
                service.enable(apiKey);
            }

        }
    }

    private void startCustomExceptionHandler() {
        List<CrashReportingService> services = getEnabledServicesForType(CrashReportingService.class);
        if(!services.isEmpty()) {
            CustomExceptionHandler customerHandler = new CustomExceptionHandler(context, services);
            customerHandler.start();
        }
    }

    private void startLogListener() {
        List<LoggingService> services = getEnabledServicesForType(LoggingService.class);
        if(!services.isEmpty()) {
            LogListener logListener = new LogListener(context, services);
            logListener.start();
        }
    }

    private void startEventTracker(){
        List<EventTrackingService> services = getEnabledServicesForType(EventTrackingService.class);
        if(!services.isEmpty()) {
            eventTracker = new EventTracker(context, services);
            eventTracker.start();
        }
    }

    @SuppressWarnings("unchecked")
    private <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
        List<T> servicesOfType = new ArrayList<>();
        for (Service service : services.values()) {
            if(clazz.isAssignableFrom(service.getClass())){
                if(service.isEnabled()) {
                    servicesOfType.add((T) service);
                }
            }
        }
        return servicesOfType;
    }

    protected void logEvent(Context context, String event){
        if(eventTracker != null) {
            eventTracker.trackEvent(context, event);
        }
    }

}
