package com.percolate.foam;

import android.content.Context;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * {@inheritDoc}
 */
class Graphite extends ServiceImpl implements EventTrackingService {

    private String host;
    private int port;
    private String apiKey;

    Graphite(Context context) {
        super(context);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enable(String url) {
        if(url.contains(":") && url.split(":").length == 2) {
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
        eventData.append(System.currentTimeMillis() / 1000L);
        eventData.append("\n");

        sendData(eventData.toString());
    }

    /**
     * Send data to Graphite server.
     * Data is sent over TCP in a new thread.
     *
     * @param graphiteEvent Event data to send to graphite.
     */
    private void sendData(final String graphiteEvent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Socket conn = null;
                try {
                    conn = new Socket(host, port);
                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
                    dos.writeBytes(graphiteEvent);
                    dos.flush();
                } catch (IOException ex) {
                    utils.logIssue("Error sending graphite event [" + graphiteEvent + "].", ex);
                } finally {
                    if(conn != null) {
                        try {
                            conn.close();
                        } catch (IOException ex) {
                            utils.logIssue("Could not close graphite socket.", ex);
                        }
                    }
                }
            }
        }).start();
    }

}
