You have a small team.  Setting up crash reporting tools, event tracking tools, and log management services
is not what you want to spend your hours doing.  You know these are important to the health of your application,
but you just want to code.

That's where Foam comes into play.  Want to use Flurry and Papertrail in your app?  No problem:
```java
    @FoamApiKeys(
        flurry = "S6S7S8S9S0S1S2S3S4S5", // API Key
        papertrail = "logs2.papertrailapp.com:49999" // Server URL
    )
    public class MyApplication extends FoamApplication {
    }
```

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
| Flurry             |                  |                |


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
    )
    public class MyApplication extends FoamApplication {
    }
```

That's it.  You've just enabled all of these services for your application.  Well, almost.  Make sure you also have the following set:

1. Internet permission in your `AndroidManifest.xml`:

        <uses-permission android:name="android.permission.INTERNET" />
        
2. At least min API level 14 in `build.gradle`:

        minSdkVersion 14

3. Define your custom Application class in your `AndroidManifest.xml`:

        <application
            android:name="com.your.app.MyApplication"

4. Dependencies for Retrofit & Gson in `build.gradle`:

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

## Notes:

  - It takes time for some services to process incoming data.  There may be a delay of few hours before anything shows up.  Be patient.
  - Like other crash reporting tools, crashes are sent when the user reopens the app after a crash.  Keeping an app open to send data during a crash would lead to other problems, such as ARNs.
  - Does you application currently extend MultiDexApplication?  No problem, just use our `FoamMultiDexApplication` version instead.
  - Can't extend `FoamApplication` or `FoamMultiDexApplication` for some reason?  That's fine too.  Make your application class implement our `FoamApp` interface, and add `` to onCreate()

**Why no Crashlytics?**

  We had it.  Then we removed it.  
  Crashlytics requires some [custom setup](https://crashlytics.com/downloads).  There is no way around this.  
  After which, enabling crashlytics is a single line of code `Crashlytics.start(this)`.  
  This tool would not lessen setup, configuration, or required coding coding as it does with the other services we support.  
  If Crashlytics were to ever support manual configuration or provide an API to submit crashes, we will add it.  
  In the meantime, if you wish to use Crashlytics in your application, instructions can be found [here](https://crashlytics.com/downloads).


## TODO

**Services to add:** Loggly


## License

Open source.  Distributed under the BSD 3 license.  See [license.txt](https://github.com/percolate/foam/blob/master/license.txt) for details.
