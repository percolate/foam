package com.percolate.foam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Track custom event.
 * All services defined in your {@link FoamApiKeys} setup will receive the event.
 *
 * Example Usage:
 * <code>new FoamEvent().track(this, "my-custom-event")</code>
 *
 */
public class FoamEvent {

    /**
     * Track custom event.  Will be sent to all services defined in the
     * {@link com.percolate.foam.FoamApiKeys} annotation on your Application class.
     *
     * @param activity Activity on which the event occurred.
     * @param event Your custom event string.
     */
    public void track(Activity activity, String event){
        if(activity != null) {
            track(activity.getApplication(), activity, event);
        }
    }

    /**
     * Track custom event.  Will be sent to all services defined in the
     * {@link com.percolate.foam.FoamApiKeys} annotation on your Application class.
     *
     * @param service Service in which the event occurred.
     * @param event Your custom event string.
     */
    public void track(android.app.Service service, String event){
        if(service != null) {
            track(service.getApplication(), service, event);
        }
    }

    /**
     * Track custom event
     * @param application Application object.  Must implement {@link FoamApp}
     * @param context Service or Activity.
     * @param event Your custom event string.
     */
    void track(Application application, Context context, String event) {
        if(application != null && application instanceof FoamApp){
            FoamMain foamMain = ((FoamApp) application).getFoamMain();
            if(foamMain != null){
                foamMain.logEvent(context, event);
            }
        }
    }

}
