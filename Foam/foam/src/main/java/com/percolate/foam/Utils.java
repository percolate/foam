package com.percolate.foam;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Foam utility methods.
 *
 * Some methods were taken from Apache Commons Lang (http://commons.apache.org/proper/commons-lang/)
 * Copied over to avoid requiring additional dependencies.
 *
 */
class Utils {

    /**
     * Checks if a CharSequence is whitespace, empty ("") or null.
     *
     * @param cs the CharSequence to check, may be null
     * @return true if the CharSequence is null, empty or whitespace
     */
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

    /**
     * Checks if a CharSequence is not empty (""), not null and not whitespace only.
     *
     * @param cs the CharSequence to check, may be null
     * @return true if the CharSequence is not empty and not null and not whitespace
     */
    public static boolean isNotBlank(final CharSequence cs) {
        return !isBlank(cs);
    }

    /**
     * Trim a given string to maxStringLength if it is over maxStringLength.
     *
     * @param str String to trim
     * @param maxStringLength Max length of string to return
     * @return str, trimmed to maxStringLength.
     */
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

    /**
     * Foam error logging.  Log a warning message using the TAG "Foam".  Include stacktrace of
     * a Throwable, if provided.
     *
     * @param message Message to log
     * @param ex Optional Throwable.  Stacktrace will be printed if provided.
     */
    public static void logIssue(String message, Throwable ex){
        if(ex != null) {
            Log.w("Foam", "Foam library: problem detected: " + message, ex);
        } else {
            Log.w("Foam", "Foam library: problem detected: " + message);
        }
    }

    /**
     * Get application "label" value from Manifest.
     *
     * @param context Context
     * @return Application label from Manifest
     */
    public static String getApplicationName(Context context) {
        int stringId = context.getApplicationInfo().labelRes;
        return context.getString(stringId);
    }

    /**
     * Get versionName value from Manifest.
     *
     * @param context Context
     * @return Application versionName from Manifest
     */
    public static String getVersionName(Context context) {
        String versionName;
        try {
            versionName = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            versionName = "";
        }
        return versionName;
    }

    /**
     * Get versionCode value from Manifest.
     *
     * @param context Context
     * @return Application versionCode from Manifest
     */
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
     * Get package name from Manifest.
     *
     * @param context Context
     * @return Application package from Manifest
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
     *
     * @param context Context
     * @return Unique User+Device identifier.  Will never be null.  Can be blank.
     */
    public static @NonNull String getAndroidId(Context context) {
        String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        if(androidId == null){
            return "";
        } else {
            return androidId;
        }
    }

}
