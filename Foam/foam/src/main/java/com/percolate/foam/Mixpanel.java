package com.percolate.foam;

import android.content.Context;
import android.util.Base64;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Query;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project:
 *
 * @author brent
 */
class Mixpanel extends ServiceImpl implements EventTrackingService {

    private String projectToken;

    Mixpanel(Context context) {
        super(context);
    }

    @Override
    public void enable(String projectToken) {
        this.projectToken = projectToken;
    }

    @Override
    public boolean isEnabled() {
        return projectToken != null;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.MIXPANEL;
    }

    @Override
    public void logEvent(Context context, String eventName) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://api.mixpanel.com")
                .build();

        MixpanelService service = restAdapter.create(MixpanelService.class);

        MixpanelEvent event = new MixpanelEvent();
        event.event = eventName;
        event.properties.put("token", projectToken);
        event.properties.put("distinct_id", Utils.getAndroidId(context));

        String data = eventObjToBase64(event);

        service.trackEvent(data, new NoOpCallback());
    }

    private String eventObjToBase64(MixpanelEvent event) {
        Gson gson = new Gson();
        String json = gson.toJson(event);
        return Base64.encodeToString(json.getBytes(), Base64.DEFAULT);
    }

    private interface MixpanelService{
        @GET("/track/")
        void trackEvent(@Query("data") String data, Callback<Response> callback);
    }

    private class MixpanelEvent {
        String event;
        Map<String, String> properties = new HashMap<String, String>();
    }
}
