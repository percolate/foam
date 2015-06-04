package com.percolate.foam;

import android.app.Application;

/**
 * Provides the ability to disable all foam tracking features.
 *
 * Usage:
 * <code>
 *     new FoamDisabler().disable(getApplication());
 * </code>
 */
public class FoamDisabler {

    /**
     * Disable all foam tracking features
     * @param application Your <code>Application</code> class.
     */
    public void disable(Application application) {
        if(application != null && application instanceof FoamApp){
            FoamMain foamMain = ((FoamApp) application).getFoamMain();
            if(foamMain != null){
                foamMain.stop();
            }
        }
    }

    /**
     * Re-enable foam.
     * @param application Your <code>Application</code> class.
     */
    public void reenable(Application application) {
        if(application != null && application instanceof FoamApp){
            FoamMain foamMain = ((FoamApp) application).getFoamMain();
            if(foamMain != null){
                foamMain.start();
            }
        }
    }

}
