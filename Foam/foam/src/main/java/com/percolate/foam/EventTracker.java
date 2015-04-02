package com.percolate.foam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class EventTracker {

    private Context context;
    private List<EventTrackingService> services;

    public EventTracker(Context context, List<EventTrackingService> services) {
        this.context = context;
        this.services = services;
    }

    public void start() {
        if(context instanceof Application){
            ((Application) context).registerActivityLifecycleCallbacks(createActivityLifecycleCallback());
        }
    }

    private Application.ActivityLifecycleCallbacks createActivityLifecycleCallback() {
        return new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityResumed(Activity activity) {
                trackActivity(activity);
            }

            @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) { }
            @Override public void onActivityStarted(Activity activity) {}
            @Override public void onActivityPaused(Activity activity) { }
            @Override public void onActivityStopped(Activity activity) { }
            @Override public void onActivitySaveInstanceState(Activity activity, Bundle outState) { }
            @Override public void onActivityDestroyed(Activity activity) { }
        };
    }

    private void trackActivity(Activity activity) {
        if(shouldTrack(activity)) {
            String activityName = activity.getClass().getSimpleName();
            trackEvent(activity, activityName);
        }
    }

    protected void trackEvent(Context context, String event) {
        for (EventTrackingService service : services) {
            if (service.isEnabled()) {
                service.logEvent(context, event);
            }
        }
    }

    /**
     * Check for classes with @FoamDontTrack annotation
     */
    private boolean shouldTrack(Activity activity) {
        if(activity!=null){
            Class<? extends Activity> clazz = activity.getClass();
            if(clazz.isAnnotationPresent(FoamDontTrack.class)){
                return false;
            } else {
                for (Method method : clazz.getMethods()) {
                    if(method.isAnnotationPresent(FoamDontTrack.class)){
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
