package com.percolate.foam;

import android.content.Context;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
interface EventTrackingService extends Service {

    void logEvent(Context context, String event);

}
