package com.percolate.foam;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Copyright (c) 2015 Percolate Industries Inc. All rights reserved.
 * Project: Foam
 *
 * @author brent
 *
 * Utility methods.  Mostly taken from Apache Commons Lang source.
 * Copied over to avoid requiring large dependencies.
 */
class Utils {

    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    public static String trimToSize(String str, int maxStringLength) {
        if(str == null)
            return null;

        if(str.length() > maxStringLength) {
            StringBuilder sb = new StringBuilder(str);
            sb.setLength(maxStringLength);
            str = sb.toString();
        }
        return str;
    }

    public static void logIssue(String message, Throwable ex){
        if(ex != null) {
            Log.w("Foam", "Foam library: problem detected: " + message, ex);
        } else {
            Log.w("Foam", "Foam library: problem detected: " + message);
        }
    }

    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    public static String getVersionName(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }

    public static int getVersionCode(Context context) {
        int versionCode;
        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            versionCode = -1;
        }
        return versionCode;
    }

    /**
     * Get package name from the manifest.
     */
    public static String getApplicationPackageName(Context context) {
        String packageName = "";
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            packageName = packageInfo.packageName;
        } catch (PackageManager.NameNotFoundException ex){
            Utils.logIssue("Could not find package name.", ex);
        }
        return packageName;
    }

    /**
     * Get ANDROID_ID, which is unique to the device + user combo.  Can sometimes be null in theory.
     */
    public static String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(androidId == null){
            return "";
        } else {
            return androidId;
        }
    }
}
