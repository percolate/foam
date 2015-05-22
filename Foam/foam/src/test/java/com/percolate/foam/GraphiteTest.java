package com.percolate.foam;

import android.content.Context;

import org.junit.Test;

import java.io.IOException;
import java.net.Socket;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for Graphite.java
 */
public class GraphiteTest {

    @Test
    public void testEnable() {
        Graphite graphite = new Graphite(null);
        assertFalse(graphite.isEnabled());
        graphite.enable("unit_test_url:8080");
        assertTrue(graphite.isEnabled());
        assertEquals("unit_test_url", graphite.host);
        assertEquals(8080, graphite.port);
        assertNull(graphite.apiKey);
    }

    @Test
    public void testEnableWithApiKey() {
        Graphite graphite = new Graphite(null);
        assertFalse(graphite.isEnabled());
        graphite.enable("unit_test_api_key@unit_test_url:8080");
        assertTrue(graphite.isEnabled());
        assertEquals("unit_test_url", graphite.host);
        assertEquals(8080, graphite.port);
        assertEquals("unit_test_api_key", graphite.apiKey);
    }

    @Test
    public void testEnableBadValues() {
        Graphite graphite = new Graphite(null);
        Utils mockUtils = UnitTestUtils.mockUtils();
        graphite.utils = mockUtils;

        assertFalse(graphite.isEnabled());

        graphite.enable(null);
        assertFalse(graphite.isEnabled());

        graphite.enable("");
        assertFalse(graphite.isEnabled());

        graphite.enable("  ");
        assertFalse(graphite.isEnabled());

        graphite.enable("url_but_no_port");
        assertFalse(graphite.isEnabled());

        verify(mockUtils, times(4)).logIssue(eq("Invalid Graphite URL.  Expecting \"[key@]host:port\" format."), any(Throwable.class));
    }

    @Test
    public void testEnableBadPortNumber() {
        Graphite graphite = new Graphite(null);
        Utils mockUtils = UnitTestUtils.mockUtils();
        doCallRealMethod().when(mockUtils).isNotBlank(anyString());
        graphite.utils = mockUtils;

        graphite.enable("bad_port:808X");
        assertFalse(graphite.isEnabled());
        verify(mockUtils).logIssue(eq("Invalid port in Graphite URL [808X]"), any(NumberFormatException.class));
    }

    @Test
    public void testGetServiceType() {
        assertEquals(ServiceType.GRAPHITE, new Graphite(null).getServiceType());
    }

    @Test
    public void testLogEvent() {
        Graphite graphite = mock(Graphite.class);
        graphite.utils = UnitTestUtils.mockUtils();
        doCallRealMethod().when(graphite).logEvent(any(Context.class), anyString());
        when(graphite.getTimeStamp()).thenReturn(140000000L);
        graphite.logEvent(null, "unit_test_event");
        String expectedMessage = "com.percolate.foam.unit.testing.unit_test_event 1 140000000\n";
        verify(graphite).sendData(eq(expectedMessage));
    }

    @Test
    public void testSendUdpData() throws IOException {
        Graphite graphite = mock(Graphite.class);
        doCallRealMethod().when(graphite).sendUdpData(anyString());
        graphite.sendUdpData("test_event_string");
        verify(graphite).sendDataOverSocket(eq("test_event_string"));
    }

    @Test
    public void testSendUdpDataException() throws IOException {
        Graphite graphite = mock(Graphite.class);
        graphite.host = "unit_test_host";
        graphite.port = 8080;
        Utils mockUtils = UnitTestUtils.mockUtils();
        graphite.utils = mockUtils;
        IOException exceptionToThrow = new IOException();
        doThrow(exceptionToThrow).when(graphite).sendDataOverSocket(anyString());
        doCallRealMethod().when(graphite).sendUdpData(anyString());

        graphite.sendUdpData("test_event_string");
        verify(mockUtils).logIssue(eq("Error sending graphite event [test_event_string] to [unit_test_host:8080]."), eq(exceptionToThrow));
    }

    @Test
    public void testCloseSocketException() throws IOException {
        Graphite graphite = new Graphite(null);
        graphite.host = "unit_test_host";
        graphite.port = 8080;
        Utils mockUtils = UnitTestUtils.mockUtils();
        graphite.utils = mockUtils;
        Socket mockSocket = mock(Socket.class);
        doThrow(IOException.class).when(mockSocket).close();
        graphite.closeSocket(mockSocket);
        verify(mockUtils).logIssue(eq("Could not close graphite socket [unit_test_host:8080]."), any(IOException.class));
    }

}