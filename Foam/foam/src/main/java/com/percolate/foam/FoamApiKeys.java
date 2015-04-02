package com.percolate.foam;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
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

}
