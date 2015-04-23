package com.percolate.foam;

import android.app.Application;

/**
 * Logic to startup Foam.  Creates an instance of FoamMain and loads it with keys from
 * the FoamApiKeys annotation on the passed Application class.
 */
class FoamApplicationInit {

    /**
     * Get API keys from FoamApiKeys annotation and start up our {@link FoamMain} class
     */
    public static FoamMain init(Application application){
        FoamMain foamMain = new FoamMain(application);
        if(application.getClass().isAnnotationPresent(FoamApiKeys.class)){
            foamMain = new FoamMain(application);
            FoamApiKeys foamApiKeys = application.getClass().getAnnotation(FoamApiKeys.class);
            foamMain.init(foamApiKeys);
            foamMain.start();
        }
        return foamMain;
    }

}