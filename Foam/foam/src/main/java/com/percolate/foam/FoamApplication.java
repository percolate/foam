package com.percolate.foam;

import android.app.Application;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
public class FoamApplication extends Application implements FoamApp {

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
