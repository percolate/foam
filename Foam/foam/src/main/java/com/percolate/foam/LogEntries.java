package com.percolate.foam;

import android.content.Context;

/**
 * {@inheritDoc}
 */
class LogEntries extends UDPLoggingService {

    LogEntries(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceType getServiceType() {
        return ServiceType.LOGENTRIES;
    }


}
