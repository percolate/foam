package com.percolate.foam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Track custom event.
 * All services defined in your Application/FoamApiKeys setup ({@see FoamApiKeys}) will receive
 * the event.
 *
 * Example Usage:
 * <code>FoamEvent.track(this, "my-custom-event")</code>
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
    public static void track(Activity activity, String event){
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
    public static void track(android.app.Service service, String event){
        if(service != null) {
            track(service.getApplication(), service, event);
        }
    }

    /**
     * Track custom event
     */
    private static void track(Application application, Context context, String event) {
        if(application != null && application instanceof FoamApp){
            FoamMain foamMain = ((FoamApp) application).getFoamMain();
            if(foamMain != null){
                foamMain.logEvent(context, event);
            }
        }
    }

}
