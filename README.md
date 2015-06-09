You have a small team.  Setting up crash reporting tools, event tracking tools, and log management services
is not what you want to spend your hours doing.  You know these are important to the health of your application,
but you just want to code.

That's where Foam comes into play.  Want to use Flurry and Papertrail in your app?  No problem:
<pre>
<b>@FoamApiKeys</b>(
    <b>flurry</b> = "S6S7S8S9S0S1S2S3S4S5", // API Key
    <b>papertrail</b> = "logs2.papertrailapp.com:49999" // Server URL
)
public class MyApplication extends <b>FoamApplication</b> {
}
</pre>

Want to use Google Analytics, PagerDuty, and Logentries?  Sure...
```java
@FoamApiKeys(
    googleAnalytics = "UA-00000000-1", // Tracking ID
    pagerDuty = "3a3b3c3d3e3f3g3h3i3j3k3l3m3n3o3p", // API Key
    logentries = "data.logentries.com:12345" // Server URL
)
public class MyApplication extends FoamApplication {
}
```

You add your API Keys, we'll do the rest.

## Supported Services

| Crash Reporting    | Event Tracking   | Log Management |
|--------------------|------------------|----------------|
| PagerDuty          | Flurry           | Papertrail     |
| Papertrail         | Mixpanel         | Logentries     |
| HockeyApp          | Google Analytics |                |
| Flurry             | Graphite         |                |

**Crash Reporting:** Unhandled exceptions will be reported.

**Event Tracking:** Activity launches will be tracked.

**Log management:** Error logs from devices will be reported.

## Setup All The Things

```java
@FoamApiKeys(
    pagerDuty = "3a3b3c3d3e3f3g3h3i3j3k3l3m3n3o3p",
    papertrail = "logs2.papertrailapp.com:49999",
    hockeyApp = "b2044c3055d4066e5077f6088g7099h8",
    flurry = "S6S7S8S9S0S1S2S3S4S5"
    mixpanel = "221b331c441d551e661f771g881h991i",
    googleAnalytics = "UA-00000000-1",
    logentries = "data.logentries.com:12345",
    graphite = "[api-key@]graphite.myhost.com:2003"
)
public class MyApplication extends FoamApplication {
}
```

That's it.  You've just enabled all of these services for your application.  Well, almost.  Make sure you also have the following set:

1. Internet permission in your `AndroidManifest.xml`:

        <uses-permission android:name="android.permission.INTERNET" />

1. SDK level 14 or higher in `build.gradle`:

        minSdkVersion 14

1. Define your custom Application class in your `AndroidManifest.xml`:

        <application
            android:name="com.your.app.MyApplication"

1. Add dependencies for Foam, Retrofit, and Gson in `build.gradle`:

        compile 'com.percolate:foam:0.9.3'
        compile 'com.squareup.retrofit:retrofit:1.9.0'
        compile 'com.google.code.gson:gson:2.3'

## Where to find your API Keys:

**HockeyApp**: Click your name (top right) -> API Tokens -> Create an API token. _(permissions can be set to "Read Only")_

**Mixpanel**: Your project 'Token' can be found under "Update project settings" (gear icon on the bottom left).

**PagerDuty**: Configuration -> API Access -> Create API Key

**Papertrail**: Add a system.  You'll see "Your systems will log to &lt;url&gt;".  Use this URL.

**Logentries**: Add a new log -> Select Manual (Gear icon) -> Change "How are the logs sent?" to "Plain TCP, UDP - logs are sent via syslog." -> Click create.  You will get back "Start sending logs to &lt;url&gt;".  Use this URL.

**Google Analytics**: Create a new Property -> Select "Mobile App" -> Click "Get Tracking ID".

**Flurry**: Create an application in Flurry then add your application key. _(FlurryAnalytics-x.x.x.jar must be added manually to your project {TODO document in wiki})_

**Graphite**: Provided by the maintainer of your graphite host.

## Notes:

- It takes time for some services to process incoming data.  There may be a delay of few hours before anything shows up.  Be patient.
- Like other crash reporting tools, crashes are sent when the user reopens the app after a crash.  Keeping an app open to send data during a crash would lead to other problems, such as ANRs.
- Does you application currently extend MultiDexApplication?  No problem, just use our `FoamMultiDexApplication` version instead.
- Can't extend `FoamApplication` or `FoamMultiDexApplication` for some reason?  That's fine too.  Make your application class implement our `FoamApp` interface, and create an instance of `FoamMain` in `onCreate`.  See [FoamApplication.java](https://github.com/percolate/foam/blob/master/Foam/foam/src/main/java/com/percolate/foam/FoamApplication.java) for an example.
- If your application may be sending lots of data, you may want to set `wifiOnly = true` on the `@FoamApiKeys` annotation.
- You can use `FoamEvent#track(Activity activity, String event)` to track custom events.
- You can use `FoamDisabler#disable()` to disable Foam, and `FoamDisabler#reenable()` to turn it back on.  Useful to hook up to a "Do not track" user setting.

**Why no Crashlytics?**

  We had it.  Then we removed it.  
  Crashlytics requires some [custom setup](https://crashlytics.com/downloads).  There is no way around this.  
  After which, enabling crashlytics is a single line of code `Crashlytics.start(this)`.  
  This tool would not lessen setup, configuration, or required coding coding as it does with the other services we support.  
  If Crashlytics were to ever support manual configuration or provide an API to submit crashes, we will add it.  
  In the meantime, if you wish to use Crashlytics in your application, instructions can be found [here](https://crashlytics.com/downloads).

## TODO

**iOS version:** Coming soon  
**Services to add:** Loggly

If you would like to add a new service please create a pull request.  A good example of what is required is contained in our [Adding Graphite PR]( https://github.com/percolate/foam/pull/3).

Feel free to open a [new issue](https://github.com/percolate/foam/issues) for platforms you would like to see added.

## License

Open source.  Distributed under the BSD 3 license.  See [LICENSE](https://github.com/percolate/foam/blob/master/LICENSE) for details.
