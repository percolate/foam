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
    protected MixpanelService mixpanelService;

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
        return utils.isNotBlank(projectToken);
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
        MixpanelEvent event = new MixpanelEvent();
        event.event = eventName;
        event.properties.put("token", projectToken);
        event.properties.put("distinct_id", utils.getAndroidId(context));

        String data = eventObjToBase64(event);
        createService().trackEvent(data, new NoOpCallback());
    }


    /**
     * Lazy load instance of {@link MixpanelService}
     * @return Instance of {@link MixpanelService}.  Never null.
     */
    protected MixpanelService createService(){
        if(mixpanelService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://api.mixpanel.com")
                    .build();

            mixpanelService = restAdapter.create(MixpanelService.class);
        }
        return mixpanelService;
    }

    /**
     * Convert MixpanelEvent to a base64 string (format expected by Mixpanel).
     *
     * @param event Event data to send.
     * @return Base64 encoded version of the passed in MixpanelEvent DTO.
     */
    protected String eventObjToBase64(MixpanelEvent event) {
        Gson gson = new Gson();
        String json = gson.toJson(event);
        return toBase64(json.getBytes());
    }

    /**
     * Return given bytes as a BASE64 encoded string.
     * @param data Data to encode
     * @return Base64 encoded version of <code>data</code>
     */
    protected String toBase64(byte[] data){
        return Base64.encodeToString(data, Base64.DEFAULT);
    }

    /**
     * Retrofit service
     */
    protected interface MixpanelService{
        @GET("/track/")
        void trackEvent(@Query("data") String data, Callback<Response> callback);
    }

    /**
     * Data transfer object to send to Mixpanel
     */
    protected class MixpanelEvent {
        String event;
        Map<String, String> properties = new HashMap<String, String>();
    }
}
