package com.percolate.foam;

import android.content.Context;

import com.flurry.android.FlurryAgent;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.contains;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Foam unit tests for Flurry.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(FlurryAgent.class)
public class FlurryTest {

    @Test
    public void testEnable() {
        Flurry flurry = new Flurry(null){
            @Override
            void initFlurryAgent() {}
        };
        assertFalse(flurry.isEnabled());
        flurry.enable("unit_test_api_key");
        assertTrue(flurry.isEnabled());
    }

    @Test
    public void testEnableBadValues() {
        Flurry flurry = new Flurry(null);

        assertFalse(flurry.isEnabled());

        flurry.enable(null);
        assertFalse(flurry.isEnabled());

        flurry.enable("");
        assertFalse(flurry.isEnabled());

        flurry.enable("  ");
        assertFalse(flurry.isEnabled());
    }

    @Test
    public void testGetServiceType() {
        assertEquals(ServiceType.FLURRY, new Flurry(null).getServiceType());
    }

    @Test
    public void testLogEvent() {
        PowerMockito.mockStatic(FlurryAgent.class);

        Context mockContext = mock(Context.class);

        Flurry flurry = new Flurry(null);
        flurry.logEvent(mockContext, "test_event");

        //Switch to verify mode
        PowerMockito.verifyStatic();

        FlurryAgent.onStartSession(eq(mockContext));
        FlurryAgent.logEvent(eq("test_event"));
        FlurryAgent.onEndSession(eq(mockContext));
    }

    @Test
    public void testCheckForJarSuccess() throws ClassNotFoundException {
        PowerMockito.mockStatic(Class.class);
        Flurry flurry = new Flurry(null);
        assertTrue(flurry.checkForJar());
    }

    @Test
    public void testCheckForJarFailure() throws ClassNotFoundException {
        final ClassNotFoundException exceptionToThrow = new ClassNotFoundException("Unit Testing");
        Flurry flurry = new Flurry(null){
            @Override
            void checkForClass(String clazz) throws ClassNotFoundException {
                throw exceptionToThrow;
            }
        };
        Utils mockUtils = UnitTestUtils.mockUtils();
        flurry.utils = mockUtils;
        assertFalse(flurry.checkForJar());
        verify(mockUtils).logIssue(contains("You must add the FlurryAnalytics-x.x.x.jar file"), eq(exceptionToThrow));
    }
}