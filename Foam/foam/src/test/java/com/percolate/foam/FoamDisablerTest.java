package com.percolate.foam;

import android.app.Application;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: PostFormLib
 *
 * @author brent
 */
public class FoamDisablerTest {

    @Test
    public void testDisable() {
        FoamDisabler foamDisabler = new FoamDisabler();
        MockApplication mockApplication = new MockApplication();
        FoamMain mockFoamMain = mockApplication.getFoamMain();
        assertNotNull(mockFoamMain);

        foamDisabler.disable(mockApplication);

        verify(mockFoamMain).stop();
    }

    @Test
    public void testReenable() {
        FoamDisabler foamDisabler = new FoamDisabler();
        MockApplication mockApplication = new MockApplication();
        FoamMain mockFoamMain = mockApplication.getFoamMain();
        assertNotNull(mockFoamMain);

        foamDisabler.reenable(mockApplication);

        verify(mockFoamMain).start();
    }

    class MockApplication extends Application implements FoamApp{

        FoamMain foamMain = mock(FoamMain.class);

        @Override
        public FoamMain getFoamMain() {
            return foamMain;
        }
    }
}