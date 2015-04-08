package com.percolate.foam;

import android.content.Context;
import android.os.AsyncTask;
import android.os.SystemClock;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Class that continuously checks logcat output for error messages.
 *
 * Errors will be sent to enabled services defined in {@link #services}.
 */
class LogListener {

    private Context context;

    /* Service that will receive log events */
    private List<LoggingService> services;

    /**
     * Set to <code>true</code> to stop monitoring logcat output
     */
    private boolean stop = false;

    /**
     * Frequency to check for new log messages (ms).
     */
    private int pollFrequencyMs = 5000;

    LogListener(Context context, List<LoggingService> services){
        this.context = context;
        this.services = services;
    }

    /**
     * Start monitoring logcat output
     */
    public void start(){
        startMonitoringLogcat();
    }

    /**
     * In a new thread, continuously check new log messages and process any new errors log messages.
     */
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

    /**
     * Return output from  `logcat -d` (print and exit) command, then clear logcat using `logcat -c`
     *
     * @return New Logcat entries.
     */
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

    /**
     * Pass all error entries to all enabled services.
     * @param logs Logs to process (check if they are errors, and send them to each enabled service).
     */
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
