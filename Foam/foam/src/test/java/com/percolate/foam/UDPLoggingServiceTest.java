package com.percolate.foam;

import org.junit.Test;

import retrofit.Callback;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isNull;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for UDPLoggingService.java
 */
public class UDPLoggingServiceTest {

    @Test
    public void testEnable() {
        UDPLoggingService udpLoggingService = new UDPLoggingService(null) {
            @Override
            public ServiceType getServiceType() {
                return null;
            }
        };
        udpLoggingService.utils = UnitTestUtils.mockUtils();
        assertFalse(udpLoggingService.isEnabled());
        udpLoggingService.enable("unittest:8080");
        assertTrue(udpLoggingService.isEnabled());
    }

    @Test
    public void testEnableBadValues() {
        UDPLoggingService udpLoggingService = new UDPLoggingService(null) {
            @Override
            public ServiceType getServiceType() {
                return null;
            }
        };
        Utils.FoamLogger mockLogger = mock(Utils.FoamLogger.class);
        udpLoggingService.utils.foamLogger = mockLogger;
        assertFalse(udpLoggingService.isEnabled());

        udpLoggingService.enable("no_port_number");
        assertFalse(udpLoggingService.isEnabled());

        udpLoggingService.enable("bad_port_number:808X");
        assertFalse(udpLoggingService.isEnabled());
        verify(mockLogger).w(startsWith("Could not get port number from url"), any(NumberFormatException.class));

        udpLoggingService.enable("two_port_numbers:8080:8080");
        assertFalse(udpLoggingService.isEnabled());

    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLogEventOneParam() {
        UDPLoggingService mockUDPLoggingService = mock(UDPLoggingService.class);
        doCallRealMethod().when(mockUDPLoggingService).logEvent(anyString());
        mockUDPLoggingService.logEvent("testing single param");
        verify(mockUDPLoggingService).sendLogEvent("Log", "testing single param", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testLogEventTwoParams() {
        StoredException storedException = new StoredException(null, null, "test_thread", "test\nstack\ntrace");
        UDPLoggingService mockUDPLoggingService = mock(UDPLoggingService.class);
        doCallRealMethod().when(mockUDPLoggingService).logEvent(any(StoredException.class), any(Callback.class));
        mockUDPLoggingService.logEvent(storedException, null);
        verify(mockUDPLoggingService).sendLogEvent("test_thread", "test\nstack\ntrace", null);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendLogEvent() {
        UDPLoggingService mockUDUdpLoggingService = mock(UDPLoggingService.class);
        mockUDUdpLoggingService.utils = UnitTestUtils.mockUtils();
        Callback mockCallback= mock(Callback.class);
        when(mockUDUdpLoggingService.getSysLogFormattedDate()).thenReturn("Jan 01 23:59:59");
        doCallRealMethod().when(mockUDUdpLoggingService).sendLogEvent(anyString(), anyString(), any(Callback.class));
        mockUDUdpLoggingService.sendLogEvent("test_component", "test_message", mockCallback);
        verify(mockUDUdpLoggingService).sendDataOverUDP(("<22>Jan 01 23:59:59 UnitTesting Application test_component:test_message"), mockCallback);
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testSendLogEventNulls() {
        UDPLoggingService mockUDUdpLoggingService = mock(UDPLoggingService.class);
        mockUDUdpLoggingService.utils = UnitTestUtils.mockUtils();
        Callback mockCallback= mock(Callback.class);
        when(mockUDUdpLoggingService.getSysLogFormattedDate()).thenReturn("Jan 01 23:59:59");
        doCallRealMethod().when(mockUDUdpLoggingService).sendLogEvent(isNull(String.class), isNull(String.class), any(Callback.class));
        mockUDUdpLoggingService.sendLogEvent(null, null, mockCallback);
        verify(mockUDUdpLoggingService).sendDataOverUDP(("<22>Jan 01 23:59:59 UnitTesting Application null:null"), mockCallback);
    }
}