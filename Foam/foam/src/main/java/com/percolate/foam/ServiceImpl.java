package com.percolate.foam;

import android.content.Context;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
abstract class ServiceImpl implements Service {

    protected Context context;

    ServiceImpl(Context context){
        this.context = context;
    }

}
