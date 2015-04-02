package com.percolate.foam;

import android.content.Context;

import com.flurry.android.FlurryAgent;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class Flurry extends ServiceImpl implements EventTrackingService {

    private String applicationKey;

    public Flurry(Context context) {
        super(context);
    }

    @Override
    public void enable(String applicationKey) {
        this.applicationKey = applicationKey;

        //FlurryAgent.setLogEnabled(true);
        FlurryAgent.setVersionName(Utils.getVersionName(context));
        FlurryAgent.setUserId(Utils.getAndroidId(context));
        FlurryAgent.setCaptureUncaughtExceptions(true);  // Crashes will also be reported
        FlurryAgent.init(context, applicationKey);
    }

    @Override
    public boolean isEnabled() {
        return applicationKey != null;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.FLURRY;
    }

    @Override
    public void logEvent(Context context, String event) {
        FlurryAgent.onStartSession(context);
        FlurryAgent.logEvent(event);
        FlurryAgent.onEndSession(context);
    }

    public boolean checkForJar() {
        try {
            Class.forName("com.flurry.android.FlurryAgent", false, getClass().getClassLoader());
            return true;
        } catch (ClassNotFoundException e) {
            Utils.logIssue("\n\n\nFoam: You must add the FlurryAnalytics-x.x.x.jar file " +
                    "to your application to enable flurry analytics.  This can be found at " +
                    "https://dev.flurry.com under Applications Tab -> Select your application " +
                    "-> Manage -> 'Download SDK'.\n\n\n", null);
            return false;
        }
    }

}
