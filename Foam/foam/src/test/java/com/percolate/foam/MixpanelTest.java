package com.percolate.foam;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Foam unit tests for Mixpanel.java
 */
public class MixpanelTest {

    @Test
    public void testEnable() {
        Mixpanel mixpanel = new Mixpanel(null);
        assertFalse(mixpanel.isEnabled());
        mixpanel.enable("unit_test_key");
        assertTrue(mixpanel.isEnabled());
    }

    @Test
    public void testEnableBadValues() {
        Mixpanel mixpanel = new Mixpanel(null);
        assertFalse(mixpanel.isEnabled());

        mixpanel.enable(null);
        assertFalse(mixpanel.isEnabled());

        mixpanel.enable("");
        assertFalse(mixpanel.isEnabled());

        mixpanel.enable("  ");
        assertFalse(mixpanel.isEnabled());

    }

    @Test
    public void testGetServiceType() {
        assertEquals(ServiceType.MIXPANEL, new Mixpanel(null).getServiceType());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLogEvent() {
        Mixpanel.MixpanelService mockMixpanelService = mock(Mixpanel.MixpanelService.class);
        Mixpanel mixpanel = new Mixpanel(null){
            @Override
            protected String toBase64(byte[] data) {
                return new String(data);
            }
        };
        mixpanel.utils = UnitTestUtils.mockUtils();
        mixpanel.mixpanelService = mockMixpanelService;
        mixpanel.logEvent(null, "unittest_event");

        String expectedJson = "{\"event\":\"unittest_event\",\"properties\":{\"distinct_id\":\"123456789\"}}";
        verify(mockMixpanelService).trackEvent(eq(expectedJson), any(NoOpCallback.class));
    }

    @Test
    public void testCreateService() {
        Mixpanel mixpanel = new Mixpanel(null);
        assertNull(mixpanel.mixpanelService);
        mixpanel.createService();
        assertNotNull(mixpanel.mixpanelService);

        //Assert service object is not recreated
        Mixpanel.MixpanelService service = mixpanel.mixpanelService;
        mixpanel.createService();
        assertSame(service, mixpanel.mixpanelService);
    }

    @Test
    public void testEventObjToBase64() {
        Mixpanel mixpanel = new Mixpanel(null){
            @Override
            protected String toBase64(byte[] data) {
                return new String(data);
            }
        };
        Mixpanel.MixpanelEvent mixpanelEvent = mixpanel.new MixpanelEvent();
        mixpanelEvent.event = "test_event";
        Map<String, String> properties = new HashMap<>();
        properties.put("unit", "testing");
        properties.put("testtest", "test2");
        mixpanelEvent.properties = properties;
        String eventObjToBase64 = mixpanel.eventObjToBase64(mixpanelEvent);
        assertEquals("{\"event\":\"test_event\",\"properties\":{\"unit\":\"testing\",\"testtest\":\"test2\"}}", eventObjToBase64);
    }

}