package com.percolate.foam;

import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
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

    protected Utils utils;

    /* Service that will receive log events */
    private List<LoggingService> services;

    /* Only send events over WiFi */
    private boolean wifiOnly;

    /**
     * Set to <code>true</code> to stop monitoring logcat output
     */
    protected boolean stop = true;

    /**
     * Frequency to check for new log messages (ms).
     */
    @SuppressWarnings("FieldCanBeLocal")
    private int pollFrequencyMs = 5000;

    LogListener(Context context, List<LoggingService> services, boolean wifiOnly){
        this.context = context;
        this.utils = new Utils();
        this.services = services;
        this.wifiOnly = wifiOnly;
    }

    /**
     * Start monitoring logcat output
     */
    public void start(){
        stop = false;
        startMonitoringLogcat();
    }

    /**
     * In a new thread, continuously check new log messages and process any new errors log messages.
     */
    protected void startMonitoringLogcat() {
        new AsyncTask<Void, Void, Void>(){
            @Override
            protected Void doInBackground(Void... params) {
                while(!stop){
                    processNewLogs();
                }
                return null;
            }
        }.execute();
    }

    /**
     * Get & process new logcat messages
     */
    protected void processNewLogs() {
        List<String> logs = getNewLogs();
        processLogEntries(logs);
        utils.sleep(pollFrequencyMs);
    }

    /**
     * Return output from  `logcat -d` (print and exit) command, then clear logcat using `logcat -c`
     *
     * @return New Logcat entries.
     */
    List<String> getNewLogs(){
        List<String> logs = new ArrayList<String>();
        try {
            Process logcatProcess = runLogcatCommand("-d");
            InputStreamReader stream = new InputStreamReader(logcatProcess.getInputStream());
            BufferedReader reader = new BufferedReader(stream);

            String line;
            while ((line = reader.readLine()) != null){
                logs.add(line);
            }
            runLogcatCommand("-c"); //-c == clear logs
        }
        catch (Exception ex){
            utils.logIssue("Error trying to read logcat output", ex);
        }
        return logs;
    }

    /**
     * Execute system <code>logcat</code> command, return the {@see Process} object.
     * @param commandLineArgs Argument to pass to logcat
     * @return logcat Process.
     * @throws IOException If there was an IO problem reading from logcat.
     */
    Process runLogcatCommand(String commandLineArgs) throws IOException {
        return Runtime.getRuntime().exec(new String[]{"logcat", commandLineArgs});
    }

    /**
     * Pass all error entries to all enabled services.
     * @param logs Logs to process (check if they are errors, and send them to each enabled service).
     */
    protected void processLogEntries(List<String> logs) {
        if(!wifiOnly || utils.isOnWifi(context)) {
            for (String log : logs) {
                if (log != null && log.startsWith("E")) {
                    for (LoggingService service : services) {
                        if (service.isEnabled()) {
                            service.logEvent(log);
                        }
                    }
                }
            }
        }
    }

    /**
     * Set flat to stop monitoring logcat.  The current logcat poll will complete, then
     * monitoring will stop.
     */
    void stop(){
        stop = true;
    }

    /**
     * Used to check if this class is already running.
     * @return true if this class not not been asked to stop yet.
     */
    boolean isRunning(){
        return !stop;
    }
}
