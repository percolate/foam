package com.percolate.foam;

import android.content.Context;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for FoamMain.java
 */
public class FoamMainTest {

    @Test
    public void testInit() {
        FoamMain foamMain = new FoamMain(null);
        foamMain.init(null);
        Set<ServiceType> registeredServiceTypes = foamMain.services.keySet();
        List<ServiceType> availableServiceTypes = Arrays.asList(ServiceType.values());
        assertTrue(
            "Some service defined in ServiceType.java were not registered after calling FoamMain#init()",
            registeredServiceTypes.containsAll(availableServiceTypes)
        );
    }

    @Test
    public void testInitializeServices() {
        Flurry flurryMock = mock(Flurry.class);
        when(flurryMock.checkForJar()).thenReturn(true);

        Map<ServiceType, Service> mockServices = new HashMap<>();
        mockServices.put(ServiceType.PAGERDUTY, mock(PagerDuty.class));
        mockServices.put(ServiceType.HOCKEYAPP, mock(HockeyApp.class));
        mockServices.put(ServiceType.PAPERTRAIL, mock(PaperTrail.class));
        mockServices.put(ServiceType.MIXPANEL, mock(Mixpanel.class));
        mockServices.put(ServiceType.GOOGLE_ANALYTICS, mock(GoogleAnalytics.class));
        mockServices.put(ServiceType.FLURRY, flurryMock);
        mockServices.put(ServiceType.LOGENTRIES, mock(LogEntries.class));
        mockServices.put(ServiceType.GRAPHITE, mock(Graphite.class));

        FoamApiKeys mockApiKeys = mock(FoamApiKeys.class);
        when(mockApiKeys.pagerDuty()).thenReturn("test_pagerDuty_key");
        when(mockApiKeys.hockeyApp()).thenReturn("test_hockeyApp_key");
        when(mockApiKeys.papertrail()).thenReturn("test_papertrail_key");
        when(mockApiKeys.mixpanel()).thenReturn("test_mixpanel_key");
        when(mockApiKeys.googleAnalytics()).thenReturn("test_googleAnalytics_key");
        when(mockApiKeys.flurry()).thenReturn("test_flurry_key");
        when(mockApiKeys.logentries()).thenReturn("test_logentries_key");
        when(mockApiKeys.graphite()).thenReturn("test_graphite_key");

        FoamMain foamMain = new FoamMain(null);
        foamMain.foamApiKeys = mockApiKeys;
        foamMain.services.putAll(mockServices);
        foamMain.initializeServices();

        verify(mockServices.get(ServiceType.PAGERDUTY)).enable("test_pagerDuty_key");
        verify(mockServices.get(ServiceType.HOCKEYAPP)).enable("test_hockeyApp_key");
        verify(mockServices.get(ServiceType.PAPERTRAIL)).enable("test_papertrail_key");
        verify(mockServices.get(ServiceType.MIXPANEL)).enable("test_mixpanel_key");
        verify(mockServices.get(ServiceType.GOOGLE_ANALYTICS)).enable("test_googleAnalytics_key");
        verify(mockServices.get(ServiceType.FLURRY)).enable("test_flurry_key");
        verify(mockServices.get(ServiceType.LOGENTRIES)).enable("test_logentries_key");
        verify(mockServices.get(ServiceType.GRAPHITE)).enable("test_graphite_key");
    }

    @Test
    public void testStartNoFlurryJar() {
        Flurry flurryMock = mock(Flurry.class);
        when(flurryMock.checkForJar()).thenReturn(false);

        FoamApiKeys mockApiKeys = mock(FoamApiKeys.class);
        when(mockApiKeys.flurry()).thenReturn("test_flurry_key");

        FoamMain foamMain = new FoamMain(null);
        foamMain.foamApiKeys = mockApiKeys;
        foamMain.services.put(ServiceType.FLURRY, flurryMock);
        foamMain.initializeServices();

        verify(flurryMock, never()).enable("test_flurry_key");
    }

    @Test
    public void testStartCustomExceptionHandlerNoServices() {
        FoamMain foamMain = new FoamMain(null){
            @Override
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return new ArrayList<>();
            }
        };
        CustomExceptionHandler mockExceptionHandler = mock(CustomExceptionHandler.class);
        foamMain.customExceptionHandler = mockExceptionHandler;

        //No services yet, should not start
        foamMain.startCustomExceptionHandler();
        verify(mockExceptionHandler, never()).start();
    }

    @Test
    public void testStartCustomExceptionHandlerAlreadyRunning() {
        final List<CrashReportingService> mockServices = new ArrayList<>();
        mockServices.add(new HockeyApp(null));

        FoamMain foamMain = new FoamMain(null){
            @Override
            @SuppressWarnings("unchecked")
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return (List<T>) mockServices;
            }
        };
        CustomExceptionHandler mockExceptionHandler = mock(CustomExceptionHandler.class);
        when(mockExceptionHandler.isRunning()).thenReturn(true);
        foamMain.customExceptionHandler = mockExceptionHandler;

        foamMain.startCustomExceptionHandler();
        verify(mockExceptionHandler, never()).start();
    }

    @Test
    public void testStartCustomExceptionHandlerSuccess() {
        final List<CrashReportingService> mockServices = new ArrayList<>();
        mockServices.add(new HockeyApp(null));

        FoamMain foamMain = new FoamMain(null){
            @Override
            @SuppressWarnings("unchecked")
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return (List<T>) mockServices;
            }
        };
        CustomExceptionHandler mockExceptionHandler = mock(CustomExceptionHandler.class);
        foamMain.customExceptionHandler = mockExceptionHandler;

        foamMain.startCustomExceptionHandler();
        verify(mockExceptionHandler).start();
    }

    @Test
    public void testStartLogListenerNoServices() {
        FoamMain foamMain = new FoamMain(null){
            @Override
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return new ArrayList<>();
            }
        };
        LogListener mockLogListener = mock(LogListener.class);
        foamMain.logListener = mockLogListener;

        //No services yet, should not start
        foamMain.startLogListener();
        verify(mockLogListener, never()).start();
    }

    @Test
    public void testStartLogListenerAlreadyRunning() {
        final List<LoggingService> mockServices = new ArrayList<>();
        mockServices.add(new PaperTrail(null));

        FoamMain foamMain = new FoamMain(null){
            @Override
            @SuppressWarnings("unchecked")
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return (List<T>) mockServices;
            }
        };
        LogListener mockLogListener = mock(LogListener.class);
        when(mockLogListener.isRunning()).thenReturn(true);
        foamMain.logListener = mockLogListener;

        foamMain.startLogListener();
        verify(mockLogListener, never()).start();
    }

    @Test
    public void testStartLogListenerSuccess() {
        final List<LoggingService> mockServices = new ArrayList<>();
        mockServices.add(new PaperTrail(null));

        FoamMain foamMain = new FoamMain(null){
            @Override
            @SuppressWarnings("unchecked")
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return (List<T>) mockServices;
            }
        };
        LogListener mockLogListener = mock(LogListener.class);
        foamMain.logListener = mockLogListener;

        foamMain.startLogListener();
        verify(mockLogListener).start();
    }

    @Test
    public void testStartEventTrackerNoServices() {
        FoamMain foamMain = new FoamMain(null){
            @Override
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return new ArrayList<>();
            }
        };
        EventTracker mockEventTracker = mock(EventTracker.class);
        foamMain.eventTracker = mockEventTracker;

        //No services yet, should not start
        foamMain.startEventTracker();
        verify(mockEventTracker, never()).start();
    }

    @Test
    public void testStartEventTrackerAlreadyRunning() {
        final List<EventTrackingService> mockServices = new ArrayList<>();
        mockServices.add(new GoogleAnalytics(null));

        FoamMain foamMain = new FoamMain(null){
            @Override
            @SuppressWarnings("unchecked")
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return (List<T>) mockServices;
            }
        };
        EventTracker mockEventTracker = mock(EventTracker.class);
        when(mockEventTracker.isRunning()).thenReturn(true);
        foamMain.eventTracker = mockEventTracker;

        foamMain.startEventTracker();
        verify(mockEventTracker, never()).start();
    }

    @Test
    public void testStartEventTrackerSuccess() {
        final List<EventTrackingService> mockServices = new ArrayList<>();
        mockServices.add(new GoogleAnalytics(null));

        FoamMain foamMain = new FoamMain(null){
            @Override
            @SuppressWarnings("unchecked")
            <T extends Service> List<T> getEnabledServicesForType(Class<T> clazz) {
                return (List<T>) mockServices;
            }
        };
        EventTracker mockEventTracker = mock(EventTracker.class);
        foamMain.eventTracker = mockEventTracker;

        foamMain.startEventTracker();
        verify(mockEventTracker).start();
    }

    @Test
    public void testGetEnabledServicesForType(){
        FoamMain foamMain = new FoamMain(null);
        List response = foamMain.getEnabledServicesForType(EventTrackingService.class);
        assertNotNull(response);
        assertTrue(response.isEmpty());

        //Add 4 EventTrackingService objects.  3 enabled, 1 disabled
        EventTrackingService eventTrackingService1 = mock(EventTrackingService.class);
        EventTrackingService eventTrackingService2 = mock(EventTrackingService.class);
        EventTrackingService eventTrackingService3 = mock(EventTrackingService.class);
        EventTrackingService eventTrackingService4 = mock(EventTrackingService.class);
        when(eventTrackingService1.isEnabled()).thenReturn(true);
        when(eventTrackingService2.isEnabled()).thenReturn(true);
        when(eventTrackingService3.isEnabled()).thenReturn(false); // DISABLED!
        when(eventTrackingService4.isEnabled()).thenReturn(true);
        foamMain.services.put(ServiceType.PAGERDUTY, eventTrackingService1);
        foamMain.services.put(ServiceType.HOCKEYAPP, eventTrackingService2);
        foamMain.services.put(ServiceType.PAPERTRAIL, eventTrackingService3);
        foamMain.services.put(ServiceType.MIXPANEL, eventTrackingService4);

        //Add 2 CrashReportingService objects
        CrashReportingService crashReportingService1 = mock(CrashReportingService.class);
        CrashReportingService crashReportingService2 = mock(CrashReportingService.class);
        when(crashReportingService1.isEnabled()).thenReturn(true);
        when(crashReportingService2.isEnabled()).thenReturn(true);
        foamMain.services.put(ServiceType.FLURRY, crashReportingService1);
        foamMain.services.put(ServiceType.LOGENTRIES, crashReportingService2);

        //Add 1 LoggingService objects
        LoggingService loggingService1 = mock(LoggingService.class);
        when(loggingService1.isEnabled()).thenReturn(true);
        foamMain.services.put(ServiceType.GOOGLE_ANALYTICS, loggingService1);

        //Perform checks
        response = foamMain.getEnabledServicesForType(EventTrackingService.class);
        assertNotNull(response);
        assertEquals(3, response.size());

        response = foamMain.getEnabledServicesForType(CrashReportingService.class);
        assertNotNull(response);
        assertEquals(2, response.size());

        response = foamMain.getEnabledServicesForType(LoggingService.class);
        assertNotNull(response);
        assertEquals(1, response.size());
    }

    @Test
    public void testLogEvent() {
        FoamMain foamMain = new FoamMain(null);
        EventTracker mockEventTracker = mock(EventTracker.class);
        foamMain.eventTracker = mockEventTracker;
        foamMain.logEvent(null, "test_event");
        verify(mockEventTracker).trackEvent(any(Context.class), eq("test_event"));
    }

    @Test
    public void testStart(){
        FoamMain foamMain = mock(FoamMain.class);
        doCallRealMethod().when(foamMain).start();

        foamMain.start();

        verify(foamMain).start();
        verify(foamMain).initializeServices();
        verify(foamMain).startCustomExceptionHandler();
        verify(foamMain).startLogListener();
        verify(foamMain).startEventTracker();
        verifyNoMoreInteractions(foamMain);
    }

    @Test
    public void testStop(){
        FoamMain foamMain = new FoamMain(null);
        foamMain.stop(); // Should not throw NPE's

        EventTracker mockEventTracker = mock(EventTracker.class);
        CustomExceptionHandler mockCustomExceptionHandler = mock(CustomExceptionHandler.class);
        LogListener mockLogListener = mock(LogListener.class);
        foamMain.eventTracker = mockEventTracker;
        foamMain.customExceptionHandler = mockCustomExceptionHandler;
        foamMain.logListener = mockLogListener;

        foamMain.stop();

        verify(mockEventTracker).stop();
        verify(mockCustomExceptionHandler).stop();
        verify(mockLogListener).stop();
    }

}