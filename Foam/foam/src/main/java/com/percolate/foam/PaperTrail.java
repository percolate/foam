package com.percolate.foam;

import android.content.Context;

/**
 * {@inheritDoc}
 */
class PaperTrail extends UDPLoggingService {

    PaperTrail(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceType getServiceType() {
        return ServiceType.PAPERTRAIL;
    }

}
