package com.percolate.foam;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit test for LogListener.java
 */
public class LogListenerTest {

    @Test
    public void testStart() {
        LogListener logListener = mock(LogListener.class);
        doCallRealMethod().when(logListener).start();
        logListener.start();
        verify(logListener).startMonitoringLogcat();
    }

    @Test
    public void testProcessNewLogs() {
        LogListener logListener = mock(LogListener.class);
        logListener.utils = UnitTestUtils.mockUtils();
        doCallRealMethod().when(logListener).processNewLogs();
        logListener.processNewLogs();
        verify(logListener).getNewLogs();
        verify(logListener).processNewLogs();
    }

    @Test
    public void testProcessLogEntries() {
        LoggingService mockService = mock(LoggingService.class);
        when(mockService.isEnabled()).thenReturn(true);
        List<LoggingService> mockServices = new ArrayList<>();
        mockServices.add(mockService);
        LogListener logListener = new LogListener(null, mockServices, false);

        List<String> logs = new ArrayList<>();
        logs.add("D/Testing Debug Message"); // Should not be processed
        logs.add("I/Testing Info Message"); // Should not be processed
        logs.add("W/Testing Warning Message"); // Should not be processed
        logs.add("E/Testing Error Message"); // *Should* be processed

        logListener.processLogEntries(logs);

        verify(mockService).logEvent(logs.get(3));
        verify(mockService, times(1)).logEvent(anyString());
    }

    @Test
    public void testGetNewLogs() throws UnsupportedEncodingException {
        final Process mockLogcatProcess = mock(Process.class);
        String fakeLogs = "E/SomeApp: Foam Unit Testing Test Log Message\n"
                + "I/SomeApp: Test Second Log Message\n";
        InputStream inputStream = new ByteArrayInputStream(fakeLogs.getBytes("UTF-8"));

        when(mockLogcatProcess.getInputStream()).thenReturn(inputStream);
        LogListener logListener = new LogListener(null, null, false){
            @Override
            Process runLogcatCommand(String commandLineArgs) throws IOException {
                return mockLogcatProcess;
            }
        };
        List<String> newLogs = logListener.getNewLogs();
        assertNotNull(newLogs);
        assertEquals(2, newLogs.size());
        assertEquals("E/SomeApp: Foam Unit Testing Test Log Message", newLogs.get(0));
        assertEquals("I/SomeApp: Test Second Log Message", newLogs.get(1));
    }

    @Test
    public void testGetNewLogsException() {
        final RuntimeException exceptionToThrow = new RuntimeException("Unit Testing");
        LogListener logListener = new LogListener(null, null, false){
            @Override
            Process runLogcatCommand(String commandLineArgs) throws IOException {
                throw exceptionToThrow;
            }
        };
        Utils mockUtils = mock(Utils.class);
        logListener.utils = mockUtils;
        logListener.getNewLogs();
        verify(mockUtils).logIssue(eq("Error trying to read logcat output"), eq(exceptionToThrow));
    }

    @Test
    public void testIsRunning(){
        LogListener logListener = new LogListener(null, null, false){
            @Override
            protected void startMonitoringLogcat() {
            }
        };
        assertFalse(logListener.isRunning());
        logListener.start();
        assertTrue(logListener.isRunning());
        logListener.stop();
        assertFalse(logListener.isRunning());
    }

}