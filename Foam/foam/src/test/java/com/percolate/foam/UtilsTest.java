package com.percolate.foam;

import android.os.SystemClock;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Foam unit tests for Utils.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({SystemClock.class, Log.class})
public class UtilsTest {

    private Utils utils;
    
    @Before
    public void setUp(){
        utils = new Utils();
    }
    
    @After
    public void tearDown(){
        utils = null;
    }
    
    @Test
    public void testIsBlank() {
        assertTrue(utils.isBlank(""));
        assertTrue(utils.isBlank(" "));
        assertTrue(utils.isBlank("  "));
        assertTrue(utils.isBlank(null));
        assertTrue(utils.isBlank("\n"));

        assertFalse(utils.isBlank("test"));
        assertFalse(utils.isBlank(" test"));
        assertFalse(utils.isBlank("  test"));
        assertFalse(utils.isBlank("@"));
        assertFalse(utils.isBlank("~"));
        assertFalse(utils.isBlank("`"));
        assertFalse(utils.isBlank("\""));
    }

    @Test
    public void testIsNotBlank() {
        assertFalse(utils.isNotBlank(""));
        assertFalse(utils.isNotBlank(" "));
        assertFalse(utils.isNotBlank("  "));
        assertFalse(utils.isNotBlank(null));
        assertFalse(utils.isNotBlank("\n"));

        assertTrue(utils.isNotBlank("test"));
        assertTrue(utils.isNotBlank(" test"));
        assertTrue(utils.isNotBlank("  test"));
        assertTrue(utils.isNotBlank("@"));
        assertTrue(utils.isNotBlank("~"));
        assertTrue(utils.isNotBlank("`"));
        assertTrue(utils.isNotBlank("\""));
    }

    @Test
    public void testTrimToSize() {
        assertEquals("", utils.trimToSize("test", 0));
        assertEquals("t", utils.trimToSize("test", 1));
        assertEquals("test", utils.trimToSize("test", 4));
        assertEquals("test", utils.trimToSize("test", 5));
        assertEquals("    ", utils.trimToSize("    test    ", 4));
    }

    @Test
    public void testSleep() {
        mockStatic(SystemClock.class);
        utils.sleep(1234);
        verifyStatic();
        SystemClock.sleep(eq(1234L));
    }

    @Test
    public void testLogIssue() {
        Utils.FoamLogger mockLogger = mock(Utils.FoamLogger.class);
        utils.foamLogger = mockLogger;
        Exception exception = new RuntimeException("Unit Testing");
        utils.logIssue("Test Log Message", exception);
        verify(mockLogger).w(eq("Test Log Message"), eq(exception));
    }

    @Test
    public void testFoamLoggerNoException() {
        mockStatic(Log.class);
        Utils.FoamLogger foamLogger = new Utils().new FoamLogger();
        foamLogger.w("Testing FoamLogger", null);

        verifyStatic();
        Log.w(eq("Foam"), contains("Testing FoamLogger"));
    }

    @Test
    public void testFoamLoggerWithException() {
        mockStatic(Log.class);
        Utils.FoamLogger foamLogger = new Utils().new FoamLogger();
        Exception exception = new RuntimeException("Unit Testing");
        foamLogger.w("Testing FoamLogger with Exception", exception);

        verifyStatic();
        Log.w(eq("Foam"), contains("Testing FoamLogger with Exception"), eq(exception));
    }

}