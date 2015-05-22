package com.percolate.foam;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Foam unit tests for StoredException.java
 */
public class StoredExceptionTest {

    @Test
    public void testCreation() {
        StoredException storedException = new StoredException(ServiceType.MIXPANEL, "message", "threadName", "stackTrace");
        assertEquals(ServiceType.MIXPANEL, storedException.platform);
        assertEquals("message", storedException.message);
        assertEquals("threadName", storedException.threadName);
        assertEquals("stackTrace", storedException.stackTrace);
    }

}