package com.percolate.foam;

import android.app.Application;

/**
 * Application class to extend when using Foam.
 */
public class FoamApplication extends Application implements FoamApp {

    /**
     * Instance of FoamMain, initialized on launch.
     */
    private FoamMain foamMain;

    @Override
    public void onCreate() {
        super.onCreate();
        foamMain = FoamApplicationInit.init(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FoamMain getFoamMain() {
        return foamMain;
    }

}
