package com.percolate.foam;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import retrofit.Callback;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Foam unit tests for PageDuty.java
 */
public class PagerDutyTest {

    @Test
    public void testEnable() {
        PagerDuty pagerDuty = new PagerDuty(null);
        assertFalse(pagerDuty.isEnabled());
        pagerDuty.enable("unit_test_api_key");
        assertTrue(pagerDuty.isEnabled());
    }

    @Test
    public void testEnableBadValues() {
        PagerDuty pagerDuty = new PagerDuty(null);

        pagerDuty.enable(null);
        assertFalse(pagerDuty.isEnabled());

        pagerDuty.enable("");
        assertFalse(pagerDuty.isEnabled());

        pagerDuty.enable("  ");
        assertFalse(pagerDuty.isEnabled());
    }

    @Test
    public void testGetServiceType() {
        PagerDuty pagerDuty = new PagerDuty(null);
        assertEquals(ServiceType.PAGERDUTY, pagerDuty.getServiceType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLogEvent() {
        PagerDuty.PagerDutyService mockPagerDutyService = mock(PagerDuty.PagerDutyService.class);
        Callback mockCallback = mock(Callback.class);

        PagerDuty pagerDuty = new PagerDuty(null);
        pagerDuty.apiKey = "unit_test_api_key";
        pagerDuty.pagerDutyService = mockPagerDutyService;

        StoredException storedException = new StoredException(ServiceType.MIXPANEL, "test message", "test threadName", "test\nstack\ntrace");
        pagerDuty.logEvent(storedException, mockCallback);

        ArgumentCaptor<PagerDuty.PagerDutyEvent> eventArgumentCaptor = ArgumentCaptor.forClass(PagerDuty.PagerDutyEvent.class);
        verify(mockPagerDutyService).createEvent(eventArgumentCaptor.capture(), eq(mockCallback));
        assertEquals("unit_test_api_key", eventArgumentCaptor.getValue().service_key);
        assertEquals("trigger", eventArgumentCaptor.getValue().event_type);
        assertEquals("test message", eventArgumentCaptor.getValue().incident_key);
        assertEquals("test\nstack\ntrace", eventArgumentCaptor.getValue().description);
    }

    @Test
    public void testCreateService() {
        PagerDuty pagerDuty = new PagerDuty(null);
        assertNull(pagerDuty.pagerDutyService);
        pagerDuty.createService();
        assertNotNull(pagerDuty.pagerDutyService);

        //Assert service object is not recreated
        PagerDuty.PagerDutyService service = pagerDuty.pagerDutyService;
        pagerDuty.createService();
        assertSame(service, pagerDuty.pagerDutyService);
    }

    @Test
    public void testCreateEvent() {
        PagerDuty pagerDuty = new PagerDuty(null);
        pagerDuty.apiKey = "unit_test_api_key";
        StoredException storedException = new StoredException(ServiceType.MIXPANEL, "test message", "test threadName", "test\nstack\ntrace");
        PagerDuty.PagerDutyEvent pagerDutyEvent = pagerDuty.createEvent(storedException);
        assertEquals("unit_test_api_key", pagerDutyEvent.service_key);
        assertEquals("trigger", pagerDutyEvent.event_type);
        assertEquals("test message", pagerDutyEvent.incident_key);
        assertEquals("test\nstack\ntrace", pagerDutyEvent.description);
    }
}