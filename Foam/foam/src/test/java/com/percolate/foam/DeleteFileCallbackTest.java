package com.percolate.foam;

import android.content.Context;

import org.junit.Test;

import retrofit.RetrofitError;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.startsWith;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for DeleteFileCallback.java
 */
public class DeleteFileCallbackTest {

    @Test
    public void testSuccessMethod() {
        Context mockContext = mock(Context.class);
        DeleteFileCallback deleteFileCallback = new DeleteFileCallback(mockContext, "unit_testing_file_name.txt");
        deleteFileCallback.success(null, null);
        verify(mockContext).deleteFile(eq("unit_testing_file_name.txt"));
    }

    @Test
    public void testSuccessError() {
        Context mockContext = mock(Context.class);
        Exception exceptionToThrow = new RuntimeException("Unit Testing");
        when(mockContext.deleteFile(anyString())).thenThrow(exceptionToThrow);
        DeleteFileCallback deleteFileCallback = new DeleteFileCallback(mockContext, "unit_testing_file_name.txt");
        Utils mockUtils = UnitTestUtils.mockUtils();
        deleteFileCallback.utils = mockUtils;
        deleteFileCallback.success(null, null);
        verify(mockUtils).logIssue(startsWith("Could not delete file"), eq(exceptionToThrow));
    }

    @Test
    public void testFailureMethod() {
        DeleteFileCallback deleteFileCallback = new DeleteFileCallback(null, "unit_testing_file_name.txt");
        RetrofitError error = mock(RetrofitError.class);
        Utils mockUtils = UnitTestUtils.mockUtils();
        deleteFileCallback.utils = mockUtils;
        deleteFileCallback.failure(error);
        verify(mockUtils).logIssue(eq("RetrofitError in DeleteFileCallback.  File: [unit_testing_file_name.txt]"), eq(error));
    }
}