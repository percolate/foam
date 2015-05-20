package com.percolate.foam;

import android.content.Context;

import java.util.LinkedHashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * {@inheritDoc}
 */
class PagerDuty extends ServiceImpl implements CrashReportingService  {

    protected String apiKey;
    protected PagerDutyService pagerDutyService;

    PagerDuty(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    public void enable(String apiKey) {
        this.apiKey = apiKey;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isEnabled(){
        return utils.isNotBlank(apiKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceType getServiceType() {
        return ServiceType.PAGERDUTY;
    }

    /**
     * {@inheritDoc}
     */
    public void logEvent(StoredException storedException, Callback<Object> callback) {
        PagerDutyEvent pagerDutyEvent = createEvent(storedException);
        createService().createEvent(pagerDutyEvent, callback);
    }

    /**
     * Lazy load instance of {@link PagerDutyService}
     * @return Instance of {@link PagerDutyService}.  Never null.
     */
    PagerDutyService createService(){
        if(pagerDutyService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint("https://events.pagerduty.com")
                    .build();
            pagerDutyService = restAdapter.create(PagerDutyService.class);
        }
        return pagerDutyService;
    }

    /**
     * Retrofit service
     */
    interface PagerDutyService {
        @POST("/generic/2010-04-15/create_event.json")
        void createEvent(@Body PagerDutyEvent pagerDutyEvent, Callback<Object> callback);
    }

    /**
     * Convert a StoredException object to a PagerDutyEvent object that can be POSTed to PagerDuty.
     *
     * @param storedException Data to convert
     * @return Populated PagerDutyEvent object.
     */
    PagerDutyEvent createEvent(StoredException storedException){
        PagerDutyEvent pagerDutyEvent = new PagerDutyEvent();
        pagerDutyEvent.service_key = apiKey;
        pagerDutyEvent.event_type = "trigger";
        pagerDutyEvent.incident_key = storedException.message;
        pagerDutyEvent.description = storedException.stackTrace;
        //pagerDutyEvent.client = "";
        //pagerDutyEvent.client_url = "";
        //pagerDutyEvent.details = ...;
        return pagerDutyEvent;
    }

    /**
     * Object that will be POSTed to PagerDuty as JSON.
     */
    class PagerDutyEvent {
        protected String service_key;
        protected String event_type;
        protected String incident_key;
        protected String description;
        protected String client;
        protected String client_url;
        protected LinkedHashMap<String, Object> details;
    }

}
