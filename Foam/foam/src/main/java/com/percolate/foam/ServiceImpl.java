package com.percolate.foam;

import android.content.Context;

/**
 * Service Object.  Represents a service to which we will send data.
 */
abstract class ServiceImpl implements Service {

    protected Context context;
    protected Utils utils;

    ServiceImpl(Context context){
        this.context = context;
        this.utils = new Utils();
    }

}
