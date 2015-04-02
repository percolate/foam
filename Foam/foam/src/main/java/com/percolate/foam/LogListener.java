package com.percolate.foam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
class LogListener {

    private Context context;
    private List<LoggingService> services;

    private boolean stop = false;
    private int pollFrequencyMs = 5000;

    LogListener(Context context, List<LoggingService> services){
        this.context = context;
        this.services = services;
    }

    public void start(){
        startMonitoringLogcat();
    }

    private void startMonitoringLogcat() {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                while(!stop){
                    List<String> logs = getNewLogs();
                    processLogEntries(logs);
                    SystemClock.sleep(pollFrequencyMs);
                }
                return null;
            }
        }.execute();
    }

    public List<String> getNewLogs(){
        List<String> logs = new ArrayList<String>();
        try {
            Process logcatProcess = Runtime.getRuntime().exec(new String[]{"logcat", "-d"});
            InputStreamReader stream = new InputStreamReader(logcatProcess.getInputStream());
            BufferedReader reader = new BufferedReader(stream);

            String line;
            while ((line = reader.readLine()) != null){
                logs.add(line);
            }
            //-c == clear logs
            Runtime.getRuntime().exec(new String[]{"logcat", "-c"});
        }
        catch (Exception ex){
            Utils.logIssue("Error trying to read logcat output", ex);
        }
        return logs;
    }

    private void processLogEntries(List<String> logs) {
        for (String log : logs) {
            if(log != null && log.startsWith("E")){
                for (LoggingService service : services) {
                    if (service.isEnabled()) {
                        service.logEvent(log);
                    }
                }
            }
        }
    }

}
