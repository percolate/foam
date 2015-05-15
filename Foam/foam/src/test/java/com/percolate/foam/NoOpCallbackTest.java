package com.percolate.foam;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import retrofit.RetrofitError;
import retrofit.client.Response;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Foam unit tests for NoOpCallback.java
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(Response.class)
public class NoOpCallbackTest {

    /**
     * Retrofit's retrofit.client.Response class is `final`.  Using
     * PowerMockRunner to allow us to mock it.
     */
    @Test
    public void testSuccess() throws Exception {
        NoOpCallback noOpCallback = new NoOpCallback();
        Response mockResponse = mock(Response.class);
        noOpCallback.success(mockResponse, mockResponse);
        verifyZeroInteractions(mockResponse);
    }

    @Test
    public void testFailure() throws Exception {
        NoOpCallback noOpCallback = new NoOpCallback();
        RetrofitError mockRetrofitError = mock(RetrofitError.class);
        noOpCallback.failure(mockRetrofitError);
        verifyZeroInteractions(mockRetrofitError);
    }

}