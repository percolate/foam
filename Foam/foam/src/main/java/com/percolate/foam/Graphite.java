package com.percolate.foam;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * {@inheritDoc}
 */
class Graphite extends ServiceImpl implements EventTrackingService {

    String host;
    int port;
    String apiKey;

    Graphite(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable(String url) {
        if(utils.isNotBlank(url) && url.contains(":") && url.split(":").length == 2) {
            String first = url.split(":")[0];
            String second = url.split(":")[1];

            if (first.contains("@")) {
                this.apiKey = first.split("@")[0];
                this.host = first.split("@")[1];
            } else {
                this.host = url.split(":")[0];
            }

            try {
                this.port = Integer.parseInt(second);
            } catch (NumberFormatException ex) {
                utils.logIssue("Invalid port in Graphite URL [" + second + "]", ex);
            }
        } else {
            utils.logIssue("Invalid Graphite URL.  Expecting \"[key@]host:port\" format.", null);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return host != null && port != 0;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ServiceType getServiceType() {
        return ServiceType.GRAPHITE;
    }

    /**
     * Send data to graphite in the format "metric_path value timestamp\n".
     * Example: "com.myapp.activities.MainActivity 1 1429801333\n"
     * ApiKey will be appended to the metric_path as expected by hosted providers.
     *
     * {@inheritDoc}
     */
    @Override
    public void logEvent(Context context, String event) {
        StringBuilder eventData = new StringBuilder();
        if(utils.isNotBlank(apiKey)){
            eventData.append(apiKey);
            eventData.append(".");
        }
        eventData.append(utils.getApplicationPackageName(context));
        eventData.append(".");
        eventData.append(event);
        eventData.append(" 1 ");
        eventData.append(getTimeStamp());
        eventData.append("\n");

        sendData(eventData.toString());
    }

    /**
     * Return unix epoch for the current time
     * @return Epoch in for the current time.
     */
    long getTimeStamp() {
        return System.currentTimeMillis() / 1000L;
    }

    /**
     * Start new background thread to send data with.
     *
     * @param graphiteEvent Event data to send to graphite.
     */
    void sendData(final String graphiteEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendUdpData(graphiteEvent);
            }
        }).start();
    }

    /**
     * Send data to Graphite server.
     *
     * @param graphiteEvent Event data to send to graphite.
     */
    void sendUdpData(String graphiteEvent) {
        Socket socket = null;
        try {
            socket = sendDataOverSocket(graphiteEvent);
        } catch (IOException ex) {
            utils.logIssue("Error sending graphite event [" + graphiteEvent + "] to [" + host + ":" + port + "].", ex);
        } finally {
            closeSocket(socket);
        }
    }

    /**
     * Create new {@link DataOutputStream} to send data over.
     *
     * @param graphiteEvent Event data to send to graphite.
     * @return created {@link Socket} object.  Should be closed after calling this method.
     * @throws IOException propagates if there was a network issue.
     */
    Socket sendDataOverSocket(String graphiteEvent) throws IOException {
        Socket socket = new Socket(host, port);
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeBytes(graphiteEvent);
        dos.flush();
        return socket;
    }

    /**
     * Close the given {@link} Socket object, checking for null
     * @param socket socket to close.
     */
    void closeSocket(Socket socket) {
        if(socket != null) {
            try {
                socket.close();
            } catch (Exception ex) {
                utils.logIssue("Could not close graphite socket [" + host + ":" + port + "].", ex);
            }
        }
    }

}
