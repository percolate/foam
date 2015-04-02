package com.percolate.foam;

import android.content.Context;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import retrofit.Callback;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 */
abstract class UDPLoggingService extends ServiceImpl implements CrashReportingService, LoggingService {

    private String url;
    private String host;
    private int port = -1;

    SimpleDateFormat df = new SimpleDateFormat("MMM dd HH:mm:ss", Locale.US);
    private final String applicationName;

    UDPLoggingService(Context context){
        super(context);
        applicationName = Utils.getApplicationName(context);
    }

    @Override
    public void enable(String url) {
        this.url = url;
        if(url.contains(":") && url.split(":").length == 2) {
            host = url.split(":")[0];
            try {
                port = Integer.parseInt(url.split(":")[1]);
            } catch (NumberFormatException nfe) {
                Utils.logIssue("Could not get port number from url [" + url + "]", nfe);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return url != null && host != null && port != -1;
    }

    /**
     * Send stored exception (i.e., crashes) to UDP endpoint
     */
    public void logEvent(StoredException storedException, Callback<Object> callback){
        sendLogEvent(storedException.threadName, storedException.stackTrace);
    }

    /**
     * Send individual log messages (i.e., Log.e() calls) to UDP endpoint
     */
    public void logEvent(String log){
        sendLogEvent("Log", log);
    }

    /**
     * Create properly formated message to send over UDP that acts like a syslog message
     * syslog format: "<priority>timestamp orange_link blue_link: message"
     * Details: http://en.wikipedia.org/wiki/Syslog#Priority
     */
    private void sendLogEvent(String component, String message){
        Date now = new Date();
        String date = df.format(now);
        String syslogMessage = String.format(Locale.US, "<22>%s %s %s:%s",
                date,
                applicationName,
                component,
                message
        );
        sendDataOverUDP(syslogMessage, null);
    }

    private void sendDataOverUDP(final String syslogMessage, final DeleteFileCallback deleteFileCallback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    byte[] message = syslogMessage.getBytes();
                    InetAddress address = InetAddress.getByName(host);
                    DatagramPacket packet = new DatagramPacket(message, message.length, address, port);
                    DatagramSocket datagramSocket = new DatagramSocket();
                    datagramSocket.send(packet);
                    datagramSocket.close();
                    if(deleteFileCallback != null) {
                        deleteFileCallback.success(null, null);
                    }
                } catch (Exception ex) {
                    Utils.logIssue("Error sending UDP log message", ex);
                }
            }
        }).start();
    }
}
