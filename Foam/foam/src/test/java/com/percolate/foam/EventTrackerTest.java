package com.percolate.foam;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for EventTracker.java
 */
public class EventTrackerTest {

    @Test
    public void testStart() {
        Application mockContext = mock(Application.class);
        final Application.ActivityLifecycleCallbacks mockCallbacks = mock(Application.ActivityLifecycleCallbacks.class);

        EventTracker eventTracker = new EventTracker(mockContext, null, false){
            @Override
            Application.ActivityLifecycleCallbacks createActivityLifecycleCallback() {
                return mockCallbacks;
            }
        };

        eventTracker.start();
        verify(mockContext).registerActivityLifecycleCallbacks(mockCallbacks);
    }

    @Test
    public void testStartFailure() {
        Context mockContext = mock(Context.class);
        EventTracker eventTracker = new EventTracker(mockContext, null, false);
        Utils mockUtils = UnitTestUtils.mockUtils();
        eventTracker.utils = mockUtils;
        eventTracker.start();
        verify(mockUtils).logIssue(startsWith("EventTracker could not start"), isNull(Throwable.class));
    }

    @Test
    public void testCreateActivityLifecycleCallback() {
        EventTracker eventTracker = new EventTracker(null, null, false);
        assertNotNull(eventTracker.createActivityLifecycleCallback());
    }

    @Test
    public void testTrackActivity() {
        EventTracker eventTracker = mock(EventTracker.class);
        Activity mockActivity = mock(Activity.class);
        doCallRealMethod().when(eventTracker).trackActivity(any(Activity.class));
        when(eventTracker.shouldTrack(eq(mockActivity))).thenReturn(true);
        eventTracker.trackActivity(mockActivity);
        verify(eventTracker).trackEvent(eq(mockActivity), anyString());
    }

    @Test
    public void testTrackEvent() {
        Context mockContext = mock(Context.class);

        List<EventTrackingService> mockServices = new ArrayList<>();
        EventTrackingService mockService1 = mock(EventTrackingService.class);
        EventTrackingService mockService2 = mock(EventTrackingService.class);
        EventTrackingService mockService3 = mock(EventTrackingService.class);
        when(mockService1.isEnabled()).thenReturn(true);
        when(mockService2.isEnabled()).thenReturn(false); // Disabled Service
        when(mockService3.isEnabled()).thenReturn(true);
        mockServices.add(mockService1);
        mockServices.add(mockService2);
        mockServices.add(mockService3);

        EventTracker eventTracker = new EventTracker(mockContext, mockServices, false);
        eventTracker.trackEvent(mockContext, "unit_testing_event");

        verify(mockService1, times(1)).logEvent(eq(mockContext), eq("unit_testing_event"));
        verify(mockService2, never()).logEvent(eq(mockContext), eq("unit_testing_event"));
        verify(mockService3, times(1)).logEvent(eq(mockContext), eq("unit_testing_event"));
    }

    @Test
    public void testShouldTrackSuccess() {
        Context mockContext = mock(Context.class);
        Utils mockUtils = mock(Utils.class);
        when(mockUtils.isOnWifi(any(Context.class))).thenReturn(true);
        EventTracker eventTracker = new EventTracker(mockContext, null, false);
        eventTracker.utils = mockUtils;
        Activity activity = new Activity();
        assertTrue(eventTracker.shouldTrack(activity));
    }

    @Test
    public void testShouldTrackNoWifi() {
        Context mockContext = mock(Context.class);
        Utils mockUtils = mock(Utils.class);
        when(mockUtils.isOnWifi(any(Context.class))).thenReturn(false);
        EventTracker eventTracker = new EventTracker(mockContext, null, true); /* true = wifiOnly */
        eventTracker.utils = mockUtils;
        Activity activity = new Activity();
        assertFalse(eventTracker.shouldTrack(activity));
    }

    @Test
    public void testShouldTrackDontTrackClassAnnotation() {
        Context mockContext = mock(Context.class);
        Utils mockUtils = mock(Utils.class);
        when(mockUtils.isOnWifi(any(Context.class))).thenReturn(true);
        EventTracker eventTracker = new EventTracker(mockContext, null, false);
        eventTracker.utils = mockUtils;

        Activity activity = new DontTrackActivity();
        assertFalse(eventTracker.shouldTrack(activity));
    }

    @FoamDontTrack
    class DontTrackActivity extends Activity{}

    @Test
    public void testShouldTrackDontTrackMethodAnnotation() {
        Context mockContext = mock(Context.class);
        Utils mockUtils = mock(Utils.class);
        when(mockUtils.isOnWifi(any(Context.class))).thenReturn(true);
        EventTracker eventTracker = new EventTracker(mockContext, null, false);
        eventTracker.utils = mockUtils;

        Activity activity = new Activity(){
            @FoamDontTrack
            @SuppressWarnings("unused")
            public void myAnnotatedMethod(){}
        };
        assertFalse(eventTracker.shouldTrack(activity));
    }

}