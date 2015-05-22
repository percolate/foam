package com.percolate.foam;

import android.content.Context;

import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Foam unit tests for HockeyApp.java
 */
public class HockeyAppTest {

    @Test
    public void testEnable() {
        HockeyApp hockeyApp = new HockeyApp(null);
        assertFalse(hockeyApp.isEnabled());
        hockeyApp.enable("unit_test_api_key");
        assertTrue(hockeyApp.isEnabled());
    }

    @Test
    public void testEnableBadValues() {
        HockeyApp hockeyApp = new HockeyApp(null);
        assertFalse(hockeyApp.isEnabled());

        hockeyApp.enable(null);
        assertFalse(hockeyApp.isEnabled());

        hockeyApp.enable("");
        assertFalse(hockeyApp.isEnabled());

        hockeyApp.enable("  ");
        assertFalse(hockeyApp.isEnabled());
    }

    @Test
    public void testGetServiceType() {
        assertEquals(ServiceType.HOCKEYAPP, new HockeyApp(null).getServiceType());
    }

    @Test
    public void testCreateService() {
        HockeyApp hockeyApp = new HockeyApp(null);
        assertNull(hockeyApp.hockeyAppService);
        hockeyApp.createService();
        assertNotNull(hockeyApp.hockeyAppService);

        //Assert service object is not recreated
        HockeyApp.HockeyAppService service = hockeyApp.hockeyAppService;
        hockeyApp.createService();
        assertSame(service, hockeyApp.hockeyAppService);
    }

    @Test
    public void testGetAppIDFromResponseSimple() {
        HockeyApp hockeyApp = new HockeyApp(null);
        hockeyApp.utils = UnitTestUtils.mockUtils();

        HockeyApp.HockeyAppsDTO apps = hockeyApp.new HockeyAppsDTO();
        apps.apps = new ArrayList<>();
        apps.apps.add(createTestHockeyAppDTO("Android", "com.percolate.foam.unit.testing", "abc123"));

        String response = hockeyApp.getAppIDFromResponse(apps);
        assertEquals("abc123", response);
    }

    @Test
    public void testGetAppIDFromResponseComplex() {
        HockeyApp hockeyApp = new HockeyApp(null);
        hockeyApp.utils = UnitTestUtils.mockUtils();

        HockeyApp.HockeyAppsDTO apps = hockeyApp.new HockeyAppsDTO();
        apps.apps = new ArrayList<>();

        //Wrong identifier
        apps.apps.add(createTestHockeyAppDTO("Android", "com.percolate.foam.wrong.identifier", "abc123"));
        // iOS, not Android.  Should not match this one
        apps.apps.add(createTestHockeyAppDTO("iOS", "com.percolate.foam.unit.testing", "def456"));
        //Should match this one!
        apps.apps.add(createTestHockeyAppDTO("Android", "com.percolate.foam.unit.testing", "ghi789"));

        String response = hockeyApp.getAppIDFromResponse(apps);
        assertEquals("ghi789", response);
    }

    @Test
    public void testGetAppIDFromResponseNull() {
        HockeyApp hockeyApp = new HockeyApp(null);
        hockeyApp.utils = UnitTestUtils.mockUtils();
        assertNull(hockeyApp.getAppIDFromResponse(null));
        assertNull(hockeyApp.getAppIDFromResponse(hockeyApp.new HockeyAppsDTO()));
    }

    @Test
    public void testGetAppIDFromResponseFail() {
        HockeyApp hockeyApp = new HockeyApp(null);
        hockeyApp.utils = UnitTestUtils.mockUtils();

        HockeyApp.HockeyAppsDTO apps = hockeyApp.new HockeyAppsDTO();
        apps.apps = new ArrayList<>();

        //Wrong identifier
        apps.apps.add(createTestHockeyAppDTO("Android", "com.percolate.foam.wrong.identifier", "abc123"));
        // iOS, not Android.  Should not match this one
        apps.apps.add(createTestHockeyAppDTO("iOS", "com.percolate.foam.unit.testing", "def456"));

        String response = hockeyApp.getAppIDFromResponse(apps);
        assertNull(response);
    }

    private HockeyApp.HockeyAppDTO createTestHockeyAppDTO(String platform, String bundleIdentifier, String publicIdentifier) {
        HockeyApp.HockeyAppDTO app = new HockeyApp(null).new HockeyAppDTO();
        app.platform = platform;
        app.bundle_identifier = bundleIdentifier;
        app.public_identifier = publicIdentifier;
        return app;
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateLogEvent() {
        final Callback mockLogEventCallback = mock(Callback.class);
        final File tempFile = createTempFile();

        HockeyApp hockeyApp = new HockeyApp(null){
            @Override
            File writeHockeyAppCrashLog(String stackTrace) {
                return tempFile;
            }

            @Override
            Callback<Response> createLogEventCallback(Callback<Object> deleteStoredExceptionCallback, File logFile) {
                return mockLogEventCallback;
            }
        };

        StoredException storedException = new StoredException(ServiceType.HOCKEYAPP, "test message", "test threadName", "test\nstack\ntrace");

        HockeyApp.HockeyAppService mockService = mock(HockeyApp.HockeyAppService.class);
        hockeyApp.hockeyAppService = mockService;
        hockeyApp.createLogEvent("UnitTestingAppId", storedException, null);

        ArgumentCaptor<TypedFile> typedFileArgumentCaptor = ArgumentCaptor.forClass(TypedFile.class);
        verify(mockService).createEvent(eq("UnitTestingAppId"), typedFileArgumentCaptor.capture(), eq(mockLogEventCallback));
        assertEquals(tempFile, typedFileArgumentCaptor.getValue().file());
        assertTrue("Failed to clean up temp file [" + tempFile.getAbsolutePath() + "].", tempFile.delete());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateLogEventCallbackSuccess() {
        HockeyApp hockeyApp = new HockeyApp(null);
        Callback mockDeleteStoredExceptionCallback = mock(Callback.class);
        File tempFile = createTempFile();
        Callback callback = hockeyApp.createLogEventCallback(mockDeleteStoredExceptionCallback, tempFile);

        assertTrue(tempFile.exists());
        callback.success(null, null);
        assertFalse(tempFile.exists());
        verify(mockDeleteStoredExceptionCallback).success(any(Response.class), any(Response.class));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testCreateLogEventCallbackFailure() {
        HockeyApp hockeyApp = new HockeyApp(null);
        Callback mockDeleteStoredExceptionCallback = mock(Callback.class);
        File tempFile = createTempFile();

        Callback callback = hockeyApp.createLogEventCallback(mockDeleteStoredExceptionCallback, tempFile);

        assertTrue(tempFile.exists());
        callback.failure(null);
        assertFalse(tempFile.exists());
        verify(mockDeleteStoredExceptionCallback).failure(any(RetrofitError.class));
    }

    private File createTempFile() {
        File tempFile = null;
        try {
            tempFile = File.createTempFile("foam-unit-testing", "log");
        } catch (IOException e) {
            fail("Could not create temp file for unit test");
        }
        return tempFile;
    }

    @Test
    public void testWriteHockeyAppCrashLog() {
        Context mockContext = mock(Context.class);
        File systemTempDir = new File(System.getProperty("java.io.tmpdir"));
        when(mockContext.getCacheDir()).thenReturn(systemTempDir);
        String testStackTrace = "unit\ntesting\nstack\ntrace";

        HockeyApp hockeyApp = new HockeyApp(mockContext){
            @Override
            String getTimestamp() {
                return "Mon May 18 13:31:10 EDT 2015";
            }
        };
        hockeyApp.utils = UnitTestUtils.mockUtils();
        File file = hockeyApp.writeHockeyAppCrashLog(testStackTrace);
        assertNotNull(file);
        assertTrue(file.exists());
        assertTrue(file.canRead());
        String fileText = null;
        try {
            fileText = new Scanner(file).useDelimiter("\\A").next();
        } catch (FileNotFoundException e) {
            fail();
        }

        String expected =
            "Package: com.percolate.foam.unit.testing\n" +
            "Version Code: 111\n" +
            "Version Name: 1.1.1\n" +
            "Android: null\n" +
            "Manufacturer: null\n" +
            "Model: null\n" +
            "Date: Mon May 18 13:31:10 EDT 2015\n" +
            "\n" +
            "unit\n" +
            "testing\n" +
            "stack\n" +
            "trace";
        assertEquals(expected, fileText);

        assertTrue("Failed to clean up unit test file [" + file.getAbsolutePath() + "].", file.delete());
    }


}