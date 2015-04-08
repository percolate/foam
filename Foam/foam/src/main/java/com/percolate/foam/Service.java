package com.percolate.foam;

/**
 * Service Object.  Object is enabled if an API key (or similar) has added to the
 * {@link com.percolate.foam.FoamApiKeys} annotation for the given service.
 */
interface Service {

    /**
     * Enable this service.  Pass in the respective API Key (or similar)
     * @param value API Key (aka, Tracking ID, Application Key, Project Token, etc, etc)
     */
    public abstract void enable(String value);

    /**
     * This service is enabled
     * @return true if enabled, otherwise false.
     */
    public abstract boolean isEnabled();

    /**
     * Returns on of {@link ServiceType} that corresponds to this service.
     * @return enum.  on of {@link ServiceType}.
     */
    public abstract ServiceType getServiceType();
}
