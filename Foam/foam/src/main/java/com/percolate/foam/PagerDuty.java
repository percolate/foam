package com.percolate.foam;

import android.content.Context;

import java.util.LinkedHashMap;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.http.Body;
import retrofit.http.POST;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class PagerDuty extends ServiceImpl implements CrashReportingService  {

    private String apiKey;

    PagerDuty(Context context) {
        super(context);
    }

    public void enable(String apiKey) {
        this.apiKey = apiKey;
    }

    public boolean isEnabled(){
        return apiKey != null;
    }

    @Override
    public ServiceType getServiceType() {
        return ServiceType.PAGERDUTY;
    }

    public void logEvent(StoredException storedException, Callback<Object> callback) {
        PagerDutyEvent pagerDutyEvent = createEvent(storedException);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("https://events.pagerduty.com")
                .build();
        PagerDutyService service = restAdapter.create(PagerDutyService.class);
        service.createEvent(pagerDutyEvent, callback);
    }

    private interface PagerDutyService {
        @POST("/generic/2010-04-15/create_event.json")
        void createEvent(@Body PagerDutyEvent pagerDutyEvent, Callback<Object> callback);
    }

    private PagerDutyEvent createEvent(StoredException storedException){
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

    private class PagerDutyEvent {
        protected String service_key;
        protected String event_type;
        protected String incident_key;
        protected String description;
        protected String client;
        protected String client_url;
        protected LinkedHashMap<String, Object> details;
    }

}
