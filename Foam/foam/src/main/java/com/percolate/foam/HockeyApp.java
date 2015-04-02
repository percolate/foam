package com.percolate.foam;

import android.content.Context;
import android.os.Build;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class HockeyApp extends ServiceImpl implements CrashReportingService {

    private String apiKey;
    private SimpleDateFormat df = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US);

    HockeyApp(Context context) {
        super(context);
    }

    @Override
    public void enable(String apiKey) {
        this.apiKey = apiKey;
   }

    @Override
    public boolean isEnabled() {
        return apiKey != null;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.HOCKEYAPP;
    }

    public void logEvent(final StoredException storedException, final Callback<Object> deleteStoredExceptionCallback) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://rink.hockeyapp.net")
                .build();

        final HockeyAppService service = restAdapter.create(HockeyAppService.class);

        service.getApps(apiKey, new Callback<HockeyAppsDTO>() {
            @Override
            public void success(HockeyAppsDTO appsList, Response response) {
                String appId = getAppIDFromResponse(appsList);
                if (Utils.isNotBlank(appId)) {
                    createLogEvent(appId, service, storedException, deleteStoredExceptionCallback);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Utils.logIssue("Error getting hockeyapp list of apps", error);
            }
        });
    }

    /**
     * Return `apps[].public_identifier` for app where app.bundle_identifier == <package-name>
     */
    private String getAppIDFromResponse(HockeyAppsDTO appsList) {
        final String applicationPackageName = Utils.getApplicationPackageName(context);
        String hockeyAppApplicationId = null;
        if (appsList != null && appsList.apps != null && !appsList.apps.isEmpty()) {
            for (HockeyAppDTO app : appsList.apps) {
                if (app.bundle_identifier != null && app.public_identifier != null) {
                    if ("Android".equalsIgnoreCase(app.platform)) {
                        if (app.bundle_identifier.equals(applicationPackageName)) {
                            hockeyAppApplicationId = app.public_identifier;
                            break;
                        }
                    }
                }
            }
        }
        return hockeyAppApplicationId;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void createLogEvent(String appId, HockeyAppService service,
                                StoredException storedException,
                                final Callback<Object> deleteStoredExceptionCallback) {

        final File logFile = writeHockeyAppCrashLog(storedException.stackTrace);
        if(logFile != null && logFile.exists()) {
            final TypedFile log = new TypedFile("text/plain", logFile);
            service.createEvent(appId, log, new Callback<Response>() {
                @Override
                public void success(Response resp, Response response) {
                    logFile.delete();
                    deleteStoredExceptionCallback.success(resp, response);
                }

                @Override
                public void failure(RetrofitError error) {
                    logFile.delete();
                    deleteStoredExceptionCallback.failure(error);
                }
            });
        }
    }

    /**
     * Write log file in hockeyapps required format
     * see http://support.hockeyapp.net/kb/api/api-crashes#post-custom-crashes
     */
    private File writeHockeyAppCrashLog(String stackTrace) {
        File logFile = null;
        BufferedWriter writer;
        try {
            File outputDir = context.getCacheDir();
            logFile = File.createTempFile("hockey_app_crash_", ".log", outputDir);
            writer = new BufferedWriter(new FileWriter(logFile));
            writer.write("Package: " + Utils.getApplicationPackageName(context) + "\n");
            writer.write("Version Code: " + Utils.getVersionCode(context) + "\n");
            writer.write("Version Name: " + Utils.getVersionName(context) + "\n");
            writer.write("Android: " + Build.VERSION.RELEASE + "\n");
            writer.write("Manufacturer: " + Build.MANUFACTURER + "\n");
            writer.write("Model: " + Build.MODEL + "\n");
            writer.write("Date: " + df.format(new Date()) + "\n");
            writer.write("\n");
            writer.write(stackTrace);
            writer.close();
        } catch (Exception ex) {
            Utils.logIssue("Error witting crash report to temp log file", ex);
        }
        return logFile;
    }

    private interface HockeyAppService {

        @GET("/api/2/apps")
        void getApps(@Header("X-HockeyAppToken") String apiKey,
                     Callback<HockeyAppsDTO> callback
        );

        @Multipart
        @POST("/api/2/apps/{APP_ID}/crashes/upload")
        void createEvent(@Path("APP_ID") String appId,
                         @Part("log") TypedFile log,
                         Callback<Response> callback
        );

    }

    private class HockeyAppsDTO {
        List<HockeyAppDTO> apps;
    }

    @SuppressWarnings("UnusedDeclaration")
    private class HockeyAppDTO {
        protected Integer id;
        protected String bundle_identifier;
        protected String public_identifier;
        protected String platform;
        protected Integer release_type;
        protected String custom_release_type;
        protected String created_at;
        protected String updated_at;
        protected String minimum_os_version;
        protected Integer status;
        protected String owner;
        protected String owner_token;
        protected String company;
    }

}
