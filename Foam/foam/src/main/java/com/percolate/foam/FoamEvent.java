package com.percolate.foam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
public class FoamEvent {

    private static void track(Application application, Context context, String event) {
        if(application != null && application instanceof FoamApp){
            FoamMain foamMain = ((FoamApp) application).getFoamMain();
            if(foamMain != null){
                foamMain.logEvent(context, event);
            }
        }
    }

    public static void track(Activity activity, String event){
        if(activity != null) {
            track(activity.getApplication(), activity, event);
        }
    }

    public static void track(android.app.Service service, String event){
        if(service != null) {
            track(service.getApplication(), service, event);
        }
    }

}
