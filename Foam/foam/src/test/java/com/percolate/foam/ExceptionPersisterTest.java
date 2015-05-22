package com.percolate.foam;

import android.content.Context;
import android.support.annotation.Nullable;

import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for ExceptionPersister.java
 */
public class ExceptionPersisterTest {

    @Test
    public void testLoadAll() {
        Context mockContext = mock(Context.class);
        when(mockContext.fileList()).thenReturn(new String[]{
                // Only files starting with "FoamStoredException" should get processed
                "FoamStoredException1.log",
                "ShouldNotBeProcessed.log",
                "FoamStoredException2.log",
                "",
                null,
                "null.log",
                "BadFoamStoredException99.log",
        });

        //These will be returned in-order by our overridden loadStoredExceptionData() method
        StoredException storedException1 = new StoredException();
        StoredException storedException2 = new StoredException();
        final List<StoredException> storedExceptions = new ArrayList<>();
        storedExceptions.add(storedException1);
        storedExceptions.add(storedException2);

        ExceptionPersister exceptionPersister = new ExceptionPersister(mockContext){
            @Nullable
            @Override
            StoredException loadStoredExceptionData(String fileName) {
                return storedExceptions.remove(0);
            }
        };

        Map<String, StoredException> result = exceptionPersister.loadAll();
        assertNotNull(result);
        assertEquals(2, result.size());
        assertSame(storedException1, result.get("FoamStoredException1.log"));
        assertSame(storedException2, result.get("FoamStoredException2.log"));
    }

    @Test
    public void testLoadAllNoFiles() {
        Context mockContext = mock(Context.class);
        when(mockContext.fileList()).thenReturn(null);
        ExceptionPersister exceptionPersister = new ExceptionPersister(mockContext);
        Map<String, StoredException> result = exceptionPersister.loadAll();
        assertNotNull(result);
        assertEquals(0, result.size());
    }

    /**
     * If this test fails, any already persisted data will fail to deserialize.  In this case,
     * do not change this test, but instead make a migration path for old data potentially still
     * stored on devices.
     */
    @Test
    public void testLoadStoredExceptionData() throws IOException {
        Context mockContext = mock(Context.class);
        File tempFile = File.createTempFile("foam_unit_testing_stored_exception", ".txt");
        PrintWriter writer = new PrintWriter(tempFile);
        String storedExceptionData =
            "{" +
                "\"platform\":\"PAGERDUTY\"," +
                "\"message\":\"test message\"," +
                "\"threadName\":\"unit_testing_thread\"," +
                "\"stackTrace\":\"unit\ntesting\nstack\ntrace\"" +
            "}";
        writer.println(storedExceptionData);
        writer.flush();
        writer.close();

        FileInputStream mockFileInputStream = new FileInputStream(tempFile);
        when(mockContext.openFileInput(anyString())).thenReturn(mockFileInputStream);

        ExceptionPersister exceptionPersister = new ExceptionPersister(mockContext);
        StoredException response = null;
        try {
            response = exceptionPersister.loadStoredExceptionData("foam_unit_testing_stored_exception.txt");
        } catch (Exception ex){
            fail("Failed to deserialize StoredException from the data [" + storedExceptionData + "].  Error: " + ex);
            ex.printStackTrace();
        }
        assertNotNull(response);
        assertEquals(ServiceType.PAGERDUTY, response.platform);
        assertEquals("test message", response.message);
        assertEquals("unit_testing_thread", response.threadName);
        assertEquals("unit\ntesting\nstack\ntrace", response.stackTrace);

        assertTrue("Unable to clean up unit testing temp file [" + tempFile.getAbsolutePath() + "].", tempFile.delete());
    }

    @Test
    public void testLoadStoredExceptionDataException() throws FileNotFoundException {
        Context mockContext = mock(Context.class);
        Utils mockUtils = mock(Utils.class);
        Exception exceptionToThrow = new RuntimeException("Unit Testing");
        when(mockContext.openFileInput(anyString())).thenThrow(exceptionToThrow);
        ExceptionPersister exceptionPersister = new ExceptionPersister(mockContext);
        exceptionPersister.utils = mockUtils;
        exceptionPersister.loadStoredExceptionData("some_file.txt");
        verify(mockUtils).logIssue(eq("Could not load file [some_file.txt]"), eq(exceptionToThrow));
    }

    @Test
    public void testCloseStream() throws IOException {
        InputStream mockInputStream = mock(InputStream.class);
        ExceptionPersister exceptionPersister = new ExceptionPersister(null);
        exceptionPersister.closeStream(mockInputStream);
        verify(mockInputStream).close();
    }

    @Test
    public void testcloseStreamException() throws IOException {
        OutputStream mockOutputStream = mock(OutputStream.class);
        Utils mockUtils = mock(Utils.class);
        Exception exceptionToThrow = new IOException("Unit Testing");
        doThrow(exceptionToThrow).when(mockOutputStream).close();
        ExceptionPersister exceptionPersister = new ExceptionPersister(null);
        exceptionPersister.utils = mockUtils;
        exceptionPersister.closeStream(mockOutputStream);
        verify(mockUtils).logIssue(eq("Could not close exception storage file."), eq(exceptionToThrow));
    }

    @Test
    public void testStore() throws IOException {
        Context mockContext = mock(Context.class);
        File tempFile = File.createTempFile("foam_unit_testing_stored_exception", ".txt");
        FileOutputStream unitTestOutputStream = new FileOutputStream(tempFile);
        when(mockContext.openFileOutput(anyString(), anyInt())).thenReturn(unitTestOutputStream);

        ExceptionPersister exceptionPersister = new ExceptionPersister(mockContext);
        StoredException storedException = new StoredException(ServiceType.MIXPANEL, "unit_testing_message", "unit_testing_threadname", "unit\ntesting\nstack\ntrace");
        exceptionPersister.store(storedException);

        Scanner scanner = new Scanner(new FileReader(tempFile));
        StringBuilder fileContents = new StringBuilder();
        while(scanner.hasNext()){
            fileContents.append(scanner.next());
        }
        scanner.close();

        assertTrue(fileContents.length() > 0);
        String expected =
            "{" +
                "\"platform\":\"MIXPANEL\"," +
                "\"message\":\"unit_testing_message\"," +
                "\"threadName\":\"unit_testing_threadname\"," +
                "\"stackTrace\":\"unit\\ntesting\\nstack\\ntrace\"" +
            "}";
        assertEquals(expected, fileContents.toString());

        assertTrue("Unable to clean up unit testing temp file [" + tempFile.getAbsolutePath() + "].", tempFile.delete());
    }
}