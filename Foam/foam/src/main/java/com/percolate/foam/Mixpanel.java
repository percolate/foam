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
 * {@inheritDoc}
 */
class Mixpanel extends ServiceImpl implements EventTrackingService {

    private String projectToken;

    Mixpanel(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable(String projectToken) {
        this.projectToken = projectToken;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return projectToken != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceType getServiceType() {
        return ServiceType.MIXPANEL;
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * Convert MixpanelEvent to a base64 string (format expected by Mixpanel).
     *
     * @param event Event data to send.
     * @return Base64 encoded version of the passed in MixpanelEvent DTO.
     */
    private String eventObjToBase64(MixpanelEvent event) {
        Gson gson = new Gson();
        String json = gson.toJson(event);
        return Base64.encodeToString(json.getBytes(), Base64.DEFAULT);
    }

    /**
     * Retrofit service
     */
    private interface MixpanelService{
        @GET("/track/")
        void trackEvent(@Query("data") String data, Callback<Response> callback);
    }

    /**
     * Data transfer object to send to Mixpanel
     */
    private class MixpanelEvent {
        String event;
        Map<String, String> properties = new HashMap<String, String>();
    }
}
