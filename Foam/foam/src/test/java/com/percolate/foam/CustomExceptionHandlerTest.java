package com.percolate.foam;

import android.content.Context;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
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
    @SuppressWarnings("unchecked")
    public void testSendStoredExceptionsSimple() {
        List<CrashReportingService> mockServices = new ArrayList<>();
        CrashReportingService mockService = mock(CrashReportingService.class);
        when(mockService.isEnabled()).thenReturn(true);
        mockServices.add(mockService);

        Utils mockUtils = mock(Utils.class);
        when(mockUtils.isOnWifi(any(Context.class))).thenReturn(true);

        Context mockContext = mock(Context.class);

        final Map<String,StoredException> mockStoredExceptions = new LinkedHashMap<>();
        StoredException storedException = new StoredException();
        mockStoredExceptions.put("unit_testing.log", storedException);

        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(mockContext, mockServices, false){
            @Override
            Map<String, StoredException> getStoredExceptions() {
                return mockStoredExceptions;
            }
        };
        customExceptionHandler.utils = mockUtils;

        customExceptionHandler.sendStoredExceptions();

        ArgumentCaptor<DeleteFileCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(DeleteFileCallback.class);
        verify(mockService).logEvent(any(StoredException.class), callbackArgumentCaptor.capture());
        assertEquals("unit_testing.log", callbackArgumentCaptor.getValue().storedExceptionFileName);
        assertSame(mockContext, callbackArgumentCaptor.getValue().context);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendStoredExceptionsComplex() {
        List<CrashReportingService> mockServices = new ArrayList<>();

        CrashReportingService mockService1 = mock(CrashReportingService.class);
        CrashReportingService mockService2 = mock(CrashReportingService.class);
        CrashReportingService mockService3 = mock(CrashReportingService.class);

        when(mockService1.isEnabled()).thenReturn(true);
        when(mockService2.isEnabled()).thenReturn(false); //Disabled service
        when(mockService3.isEnabled()).thenReturn(true);

        mockServices.add(mockService1);
        mockServices.add(mockService2);
        mockServices.add(mockService3);

        Utils mockUtils = mock(Utils.class);
        when(mockUtils.isOnWifi(any(Context.class))).thenReturn(true);

        Context mockContext = mock(Context.class);

        final Map<String,StoredException> mockStoredExceptions = new LinkedHashMap<>();
        StoredException storedException1 = new StoredException();
        StoredException storedException2 = new StoredException();
        mockStoredExceptions.put("unit_testing_1.log", storedException1);
        mockStoredExceptions.put("unit_testing_2.log", storedException2);

        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(mockContext, mockServices, false){
            @Override
            Map<String, StoredException> getStoredExceptions() {
                return mockStoredExceptions;
            }
        };
        customExceptionHandler.utils = mockUtils;

        customExceptionHandler.sendStoredExceptions();

        // mockService1 should be passed storedException1 and storedException2
        ArgumentCaptor<DeleteFileCallback> callbackArgumentCaptor = ArgumentCaptor.forClass(DeleteFileCallback.class);
        verify(mockService1, times(2)).logEvent(any(StoredException.class), callbackArgumentCaptor.capture());
        List<DeleteFileCallback> callbacksExecuted = callbackArgumentCaptor.getAllValues();
        assertEquals("unit_testing_1.log", callbacksExecuted.get(0).storedExceptionFileName);
        assertEquals("unit_testing_2.log", callbacksExecuted.get(1).storedExceptionFileName);
        assertSame(mockContext, callbacksExecuted.get(0).context);
        assertSame(mockContext, callbacksExecuted.get(1).context);

        // mockService2 is disabled
        verify(mockService2, never()).logEvent(any(StoredException.class), any(Callback.class));

        // mockService3 should be passed storedException1 and storedException2
        verify(mockService3, times(2)).logEvent(any(StoredException.class), callbackArgumentCaptor.capture());
        callbacksExecuted = callbackArgumentCaptor.getAllValues();
        assertEquals("unit_testing_1.log", callbacksExecuted.get(0).storedExceptionFileName);
        assertEquals("unit_testing_2.log", callbacksExecuted.get(1).storedExceptionFileName);
        assertSame(mockContext, callbacksExecuted.get(0).context);
        assertSame(mockContext, callbacksExecuted.get(1).context);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendStoredExceptionsNoWifi() {
        List<CrashReportingService> mockServices = new ArrayList<>();
        CrashReportingService mockService = mock(CrashReportingService.class);
        mockServices.add(mockService);

        Utils mockUtils = mock(Utils.class);
        when(mockUtils.isOnWifi(any(Context.class))).thenReturn(false); /* Return WiFi is off */

        Context mockContext = mock(Context.class);

        final Map<String,StoredException> mockStoredExceptions = new LinkedHashMap<>();
        mockStoredExceptions.put("unit_testing.log", new StoredException());

        boolean wifiOnly = true;
        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(mockContext, mockServices, wifiOnly){
            @Override
            Map<String, StoredException> getStoredExceptions() {
                return mockStoredExceptions;
            }
        };
        customExceptionHandler.utils = mockUtils;

        customExceptionHandler.sendStoredExceptions();

        verifyZeroInteractions(mockService);
    }

    @Test
    public void testGetStoredExceptions() {
        ExceptionPersister mockExceptionPersister = mock(ExceptionPersister.class);
        CustomExceptionHandler customExceptionHandler = new CustomExceptionHandler(null, null, false);
        customExceptionHandler.exceptionPersister = mockExceptionPersister;
        customExceptionHandler.getStoredExceptions();
        verify(mockExceptionPersister).loadAll();
    }
}