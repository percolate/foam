package com.percolate.foam;

import android.content.Context;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class PaperTrail extends UDPLoggingService {

    PaperTrail(Context context) {
        super(context);
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.PAPERTRAIL;
    }

}
