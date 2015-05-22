package com.percolate.foam;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for CustomExceptionHandler.java
 */
public class CustomExceptionHandlerTest {

    @Test
    public void testStart() {
        CustomExceptionHandler customExceptionHandler = mock(CustomExceptionHandler.class);
        doCallRealMethod().when(customExceptionHandler).start();
        customExceptionHandler.start();
        verify(customExceptionHandler).sendStoredExceptions();
        assertSame(Thread.getDefaultUncaughtExceptionHandler(), customExceptionHandler);
    }

    @Test
    public void testUncaughtException() {
        Thread.UncaughtExceptionHandler mockDefaultExceptionHandler = mock(Thread.UncaughtExceptionHandler.class);
        CustomExceptionHandler customExceptionHandler = mock(CustomExceptionHandler.class);
        doCallRealMethod().when(customExceptionHandler).uncaughtException(any(Thread.class), any(Throwable.class));
        customExceptionHandler.defaultHandler = mockDefaultExceptionHandler;
        Thread thread = new Thread();
        Throwable throwable = new Throwable();
        customExceptionHandler.uncaughtException(thread, throwable);

        verify(customExceptionHandler).storeException(eq(thread), eq(throwable));
        verify(mockDefaultExceptionHandler).uncaughtException(eq(thread), eq(throwable));
    }

    @Test
    public void testStoreException() {
        List<CrashReportingService> mockServices = new ArrayList<>();

        CrashReportingService mockService1 = mock(CrashReportingService.class);
        CrashReportingService mockService2 = mock(CrashReportingService.class);
        CrashReportingService mockService3 = mock(CrashReportingService.class);

        when(mockService1.isEnabled()).thenReturn(true);
        when(mockService2.isEnabled()).thenReturn(false); //Disabled service
        when(mockService3.isEnabled()).thenReturn(true);

        when(mockService1.getServiceType()).thenReturn(ServiceType.HOCKEYAPP);
        when(mockService2.getServiceType()).thenReturn(ServiceType.PAGERDUTY);
        when(mockService3.getServiceType()).thenReturn(ServiceType.PAPERTRAIL);

        mockServices.add(mockService1);
        mockServices.add(mockService2);
        mockServices.add(mockService3);

        ExceptionPersister mockExceptionPersister = mock(ExceptionPersister.class);
        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(null, mockServices, false){
            @Override
            String getStackTraceString(Throwable ex) {
                return "unit\ntesting\nstack\ntrace";
            }
        };
        customExceptionHandler.exceptionPersister = mockExceptionPersister;

        Thread thread = new Thread("unit_testing_thread");
        Throwable throwable = new RuntimeException("unit testing exception");
        customExceptionHandler.storeException(thread, throwable);

        ArgumentCaptor<StoredException> storedExceptionArgumentCaptor = ArgumentCaptor.forClass(StoredException.class);
        verify(mockExceptionPersister, times(2)).store(storedExceptionArgumentCaptor.capture());

        List<StoredException> capturedStoredExceptions = storedExceptionArgumentCaptor.getAllValues();
        assertNotNull(capturedStoredExceptions);
        assertEquals(2, capturedStoredExceptions.size());

        assertEquals(ServiceType.HOCKEYAPP, capturedStoredExceptions.get(0).platform);
        assertEquals("unit testing exception", capturedStoredExceptions.get(0).message);
        assertEquals("unit\ntesting\nstack\ntrace", capturedStoredExceptions.get(0).stackTrace);
        assertEquals("unit_testing_thread", capturedStoredExceptions.get(0).threadName);

        assertEquals(ServiceType.PAPERTRAIL, capturedStoredExceptions.get(1).platform);
        assertEquals("unit testing exception", capturedStoredExceptions.get(1).message);
        assertEquals("unit\ntesting\nstack\ntrace", capturedStoredExceptions.get(1).stackTrace);
        assertEquals("unit_testing_thread", capturedStoredExceptions.get(1).threadName);

    }

    @Test
    public void testSendStoredExceptions() {
        
    }
}