package com.percolate.foam;

import android.app.Application;
import android.content.Context;

import org.junit.Test;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Foam unit test for FoamEvent.java
 */
public class FoamEventTest {

    @Test
    public void testTrack() {
        FoamEvent foamEvent = new FoamEvent();
        Context mockContext = mock(Context.class);
        FoamMain mockFoamMain = mock(FoamMain.class);
        MockApplication mockApplication = new MockApplication(mockFoamMain);

        foamEvent.track(mockApplication, mockContext, "unit_testing_event");
        verify(mockFoamMain).logEvent(eq(mockContext), eq("unit_testing_event"));
    }

    class MockApplication extends Application implements FoamApp {

        private FoamMain foamMain;

        MockApplication(FoamMain foamMain){
            this.foamMain = foamMain;
        }

        @Override
        public FoamMain getFoamMain() {
            return foamMain;
        }
    }

}