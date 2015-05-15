package com.percolate.foam;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Foam unit tests for utils.java
 */
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
    public void testIsNotBlank(){
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
    public void testTrimToSize() throws Exception {
        assertEquals("", utils.trimToSize("test", 0));
        assertEquals("t", utils.trimToSize("test", 1));
        assertEquals("test", utils.trimToSize("test", 4));
        assertEquals("test", utils.trimToSize("test", 5));
        assertEquals("    ", utils.trimToSize("    test    ", 4));
    }

    @Test
    public void testLogIssue() throws Exception {
        //TODO
    }

    @Test
    public void testGetApplicationName() throws Exception {
        //TODO
    }

    @Test
    public void testGetVersionName() throws Exception {
        //TODO
    }

    @Test
    public void testGetVersionCode() throws Exception {
        //TODO
    }

    @Test
    public void testGetApplicationPackageName() throws Exception {
        //TODO
    }

    @Test
    public void testGetAndroidId() throws Exception {
        //TODO
    }

    @Test
    public void testIsOnWifi() throws Exception {
        //TODO
    }
}