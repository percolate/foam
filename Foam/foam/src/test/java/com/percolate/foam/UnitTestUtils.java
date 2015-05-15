package com.percolate.foam;

import android.content.Context;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Foam common unit test utility methods
 */
public class UnitTestUtils {

    /**
     * Setup mock instance of {@link Utils}.
     */
    public static Utils mockUtils(){
        final Utils mockUtils = mock(Utils.class);
        when(mockUtils.getApplicationName(any(Context.class))).thenReturn("UnitTesting Application");
        when(mockUtils.getAndroidId(any(Context.class))).thenReturn("123456789");
        return mockUtils;
    }

}
