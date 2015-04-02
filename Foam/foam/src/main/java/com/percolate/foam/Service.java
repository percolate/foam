package com.percolate.foam;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
interface Service {

    public abstract void enable(String value);

    public abstract boolean isEnabled();

    public abstract ServiceType getServiceType();
}
