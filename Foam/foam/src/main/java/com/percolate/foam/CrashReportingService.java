package com.percolate.foam;

import retrofit.Callback;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
interface CrashReportingService extends Service {

    void logEvent(StoredException storedException, Callback<Object> callback);

}
