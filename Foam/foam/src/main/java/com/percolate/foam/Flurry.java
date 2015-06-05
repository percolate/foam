package com.percolate.foam;

import android.content.Context;

import com.flurry.android.FlurryAgent;

/**
 * Flurry Service
 *
 * {@inheritDoc}
 */
class Flurry extends ServiceImpl implements EventTrackingService {

    private String applicationKey;

    public Flurry(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable(String applicationKey) {
        this.applicationKey = applicationKey;

        if(utils.isNotBlank(this.applicationKey)) {
            initFlurryAgent();
        }
    }

    /**
     * Perform Flurry setup/init
     */
    void initFlurryAgent() {
        //FlurryAgent.setLogEnabled(true);
        FlurryAgent.setVersionName(utils.getVersionName(context));
        FlurryAgent.setUserId(utils.getAndroidId(context));
        FlurryAgent.setCaptureUncaughtExceptions(true);  // Crashes will also be reported
        FlurryAgent.init(context, applicationKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return utils.isNotBlank(applicationKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceType getServiceType() {
        return ServiceType.FLURRY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logEvent(Context context, String event) {
        FlurryAgent.onStartSession(context);
        FlurryAgent.logEvent(event);
        FlurryAgent.onEndSession(context);
    }

    /**
     * Check if the FlurryAnalytics-x.x.x.jar file is on the classpath.  This is checked before
     * the Flurry service is enabled.  If the Flurry classes cannot be found a warning will
     * be logged and the service will not be enabled.
     * @return true if FlurryAgent was found on the classpath.
     */
    public boolean checkForJar() {
        try {
            checkForClass("com.flurry.android.FlurryAgent");
            return true;
        } catch (ClassNotFoundException ex) {
            utils.logIssue("\n\n\nFoam: You must add the FlurryAnalytics-x.x.x.jar file " +
                    "to your application to enable flurry analytics.  This can be found at " +
                    "https://dev.flurry.com under Applications Tab -> Select your application " +
                    "-> Manage -> 'Download SDK'.\n\n\n", ex);
            return false;
        }
    }

    /**
     * Try to load the given class using <code>Class#forName()</code>.
     * @throws ClassNotFoundException if the class could not be loaded
     * @param clazz absolute package name for class to load.
     */
    void checkForClass(String clazz) throws ClassNotFoundException {
        Class.forName(clazz, false, getClass().getClassLoader());
    }

}
