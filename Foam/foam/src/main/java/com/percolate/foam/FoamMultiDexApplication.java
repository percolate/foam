package com.percolate.foam;

import android.support.multidex.MultiDexApplication;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
public class FoamMultiDexApplication extends MultiDexApplication implements FoamApp {

    private FoamMain foamMain;

    @Override
    public void onCreate() {
        super.onCreate();
        foamMain = FoamApplicationInit.init(this);
    }

    @Override
    public FoamMain getFoamMain() {
        return foamMain;
    }
}
