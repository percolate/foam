package com.percolate.foam;

import android.content.Context;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Foam unit tests for GoogleAnalytics.java
 */
public class GoogleAnalyticsTest {

    @Test
    public void testEnable() {
        GoogleAnalytics googleAnalytics = new GoogleAnalytics(null);
        assertFalse(googleAnalytics.isEnabled());
        googleAnalytics.enable("unit_test_tracking_id");
        assertTrue(googleAnalytics.isEnabled());
    }

    @Test
    public void testEnableBadValues() {
        GoogleAnalytics googleAnalytics = new GoogleAnalytics(null);
        assertFalse(googleAnalytics.isEnabled());

        googleAnalytics.enable(null);
        assertFalse(googleAnalytics.isEnabled());

        googleAnalytics.enable("");
        assertFalse(googleAnalytics.isEnabled());

        googleAnalytics.enable("   ");
        assertFalse(googleAnalytics.isEnabled());
    }

    @Test
    public void testGetServiceType() {
        assertEquals(ServiceType.GOOGLE_ANALYTICS, new GoogleAnalytics(null).getServiceType());
    }

    @Test
    public void testLogEvent() {
        GoogleAnalytics googleAnalytics = new GoogleAnalytics(null){
            @Override
            String createPayloadData(String event) {
                return "test_payload";
            }
        };
        GoogleAnalytics.GoogleAnalyticsService mockService = mock(GoogleAnalytics.GoogleAnalyticsService.class);
        googleAnalytics.googleAnalyticsService = mockService;
        googleAnalytics.logEvent(null, "test_event");
        verify(mockService).createEvent(eq("test_payload"), any(NoOpCallback.class));
    }

    @Test
    public void testCreateService() {
        GoogleAnalytics googleAnalytics = new GoogleAnalytics(null);
        assertNull(googleAnalytics.googleAnalyticsService);
        googleAnalytics.createService();
        assertNotNull(googleAnalytics.getServiceType());

        //Assert service object is not recreated
        GoogleAnalytics.GoogleAnalyticsService service = googleAnalytics.googleAnalyticsService;
        googleAnalytics.createService();
        assertSame(service, googleAnalytics.googleAnalyticsService);
    }

    @Test
    public void testCreatePayloadData() {
        GoogleAnalytics googleAnalytics = new GoogleAnalytics(null);
        googleAnalytics.trackingId = "tracking_id";
        googleAnalytics.utils = UnitTestUtils.mockUtils();

        String response = googleAnalytics.createPayloadData("test_event");
        String expected = "v=1&" +
                "tid=tracking_id&" +
                "cid=25f9e794-323b-3538-85f5-181f1b624d0b&" +
                "t=screenview&" +
                "an=UnitTesting Application&" +
                "av=1.1.1&" +
                "aid=com.percolate.foam.unit.testing" +
                "&cd=test_event";
        assertEquals(expected, response);
    }

    @Test
    public void testCreatePayloadDataErrors(){
        GoogleAnalytics googleAnalytics = new GoogleAnalytics(null);
        Utils mockUtils = UnitTestUtils.mockUtils();
        doThrow(RuntimeException.class).when(mockUtils).getAndroidId(any(Context.class));
        googleAnalytics.utils = mockUtils;
        googleAnalytics.createPayloadData("testing_exception");
        verify(mockUtils).logIssue(eq("Error creating google analytics payload data"), any(RuntimeException.class));
    }
}