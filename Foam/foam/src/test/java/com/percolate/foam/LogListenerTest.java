package com.percolate.foam;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.Matchers.anyString;
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
}