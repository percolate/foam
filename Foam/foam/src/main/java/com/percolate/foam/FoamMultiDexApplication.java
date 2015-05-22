package com.percolate.foam;

import android.support.multidex.MultiDexApplication;

/**
 * Application class to extend when using Foam if your application needs to also extend
 * MultiDexApplication.
 */
public class FoamMultiDexApplication extends MultiDexApplication implements FoamApp {

    /**
     * Instance of FoamMain, initialized on launch.
     */
    private FoamMain foamMain;

    @Override
    public void onCreate() {
        super.onCreate();
        foamMain = new FoamApplicationInit(this).init();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FoamMain getFoamMain() {
        return foamMain;
    }
}
