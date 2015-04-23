package com.percolate.foam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to enable multiple services for an application.
 *
 * Example usage:
 * <code>
 *   \@FoamApiKeys(
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
 * </code>
 *
 * You can also enable a subset of services:
 * <code>
 *   \@FoamApiKeys(
 *     mixpanel = "221b331c441d551e661f771g881h991i",
 *     hockeyApp = "b2044c3055d4066e5077f6088g7099h8",
 *     papertrail = "logs2.papertrailapp.com:49999"
 *   )
 *   public class MyApplication extends FoamApplication {
 *   }
 * </code>
 *
 * For further instructions including information on where to find API keys for various services,
 * see: https://github.com/percolate/foam
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FoamApiKeys {

    String hockeyApp() default "";
    String papertrail() default "";
    String pagerDuty() default "";
    String logentries() default "";
    String mixpanel() default "";
    String googleAnalytics() default "";
    String flurry() default "";
    String graphite() default "";

    boolean wifiOnly() default false;

}
