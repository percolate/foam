package com.percolate.foam;

import android.app.Application;

/**
 * Logic to startup Foam.  Creates an instance of FoamMain and loads it with keys from
 * the FoamApiKeys annotation on the passed Application class.
 */
class FoamApplicationInit {

    FoamMain foamMain;
    Application application;
    Utils utils;

    public FoamApplicationInit(Application application){
        this.foamMain = new FoamMain(application);
        this.application = application;
        this.utils = new Utils();
    }

    /**
     * Get API keys from FoamApiKeys annotation and start up our {@link FoamMain} class
     * @return instance of FoamMain.
     */
    public FoamMain init(){
        if(application != null) {
            FoamApiKeys foamApiKeys = getFoamApiKeys();
            if (foamApiKeys != null) {
                foamMain.init(foamApiKeys);
                foamMain.start();
            } else {
                utils.logIssue("Please add @FoamApiKeys to " + application.getClass().getName(), null);
            }
        }
        return foamMain;
    }

    /**
     * Get API keys from Annotated <code>Application</code> class
     * @return FoamApiKeys
     */
    FoamApiKeys getFoamApiKeys() {
        if(application.getClass().isAnnotationPresent(FoamApiKeys.class)) {
            return application.getClass().getAnnotation(FoamApiKeys.class);
        }
        return null;
    }

}
