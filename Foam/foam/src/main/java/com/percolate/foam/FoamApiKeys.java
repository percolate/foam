package com.percolate.foam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to enable multiple services for an application.
 *
 * <p>Example usage:</p>
 * <pre>
 *   &#64;FoamApiKeys(
 *     pagerDuty = "3a3b3c3d3e3f3g3h3i3j3k3l3m3n3o3p",
 *     papertrail = "logs2.papertrailapp.com:49999",
 *     hockeyApp = "b2044c3055d4066e5077f6088g7099h8",
 *     flurry = "S6S7S8S9S0S1S2S3S4S5"
 *     mixpanel = "221b331c441d551e661f771g881h991i",
 *     googleAnalytics = "UA-00000000-1",
 *     logentries = "data.logentries.com:12345",
 *   )
 *   public class MyApplication extends FoamApplication {
 *   }
 * </pre>
 *
 * You can also enable a subset of services:
 * <pre>
 *   &#64;FoamApiKeys(
 *     mixpanel = "221b331c441d551e661f771g881h991i",
 *     hockeyApp = "b2044c3055d4066e5077f6088g7099h8",
 *     papertrail = "logs2.papertrailapp.com:49999"
 *   )
 *   public class MyApplication extends FoamApplication {
 *   }
 * </pre>
 *
 * For further instructions including information on where to find API keys for various services,
 * see: <a href="https://github.com/percolate/foam">https://github.com/percolate/foam</a>
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FoamApiKeys {

    /**
     * <b>HockeyApp API token.</b>
     * <p>Instructions: Click your name (top right) -&gt; API Tokens -&gt; Create an API token.
     * (permissions can be set to "Read Only")</p>
     * @return HockeyApp API token.
     */
    String hockeyApp() default "";

    /**
     * <b>Papertrail URL</b>
     * <p>Instructions: Add a system. You'll see "Your systems will log to &lt;url&gt;".
     * Use this URL.</p>
     * @return Papertrail URL
     */
    String papertrail() default "";

    /**
     * <b>PagerDuty API Key</b>
     * <p>Instructions: Configuration -&gt; API Access -&gt; Create API Key</p>
     * @return PagerDuty API Key
     */
    String pagerDuty() default "";

    /**
     * <b>Logentries Server URL</b>
     * <p>Instructions Add a new log -&gt; Select Manual (Gear icon) -&gt; Change
     * "How are the logs sent?" to "Plain TCP, UDP - logs are sent via syslog." -&gt; Click create.
     * You will get back "Start sending logs to &lt;url&gt;". Use this URL.</p>
     * @return Logentries Server URL
     */
    String logentries() default "";

    /**
     * <b>Mixpanel project token</b>
     * <p>Instructions: Your project 'Token' can be found under "Update project settings" (gear
     * icon on the bottom left).</p>
     * @return Mixpanel project token
     */
    String mixpanel() default "";

    /**
     * <b>Google Analytics Tracking ID</b>
     * <p>Instructions: Create a new Property -&gt; Select "Mobile App" -&gt; Click
     * "Get Tracking ID". </p>
     * @return Google Analytics Tracking ID
     */
    String googleAnalytics() default "";

    /**
     * <b>Flurry Application Key</b>
     * <p>Instructions: Create an application in Flurry then add your application key.</p>
     * <p>Note: FlurryAnalytics-x.x.x.jar must be added manually to your project.</p>
     * @return Flurry Application Key
     */
    String flurry() default "";

    /**
     * <b>Graphite Server URL</b>
     * <p>Instructions: URL will be provided by the maintainer of your graphite host.</p>
     * @return Graphite Server URL
     */
    String graphite() default "";

    /**
     * Only send data when client is connected to WiFi.
     * @return true if data should only be sent over wifi.
     */
    boolean wifiOnly() default false;

}
