package com.percolate.foam;

import android.content.Context;

/**
 * Service that supports receiving event tracking data.
 */
interface EventTrackingService extends Service {

    /**
     * Send the given event to the backing service.
     *
     * @param context Context.
     * @param event Event to track.  This will be sent to the service represented by this class.
     */
    void logEvent(Context context, String event);

}
