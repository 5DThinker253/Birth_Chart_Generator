package com.ejs.birthchart.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class prefUtils {
    public static final String PREF_SETTINGS = "pref_setting";
    public static final String PREF_ECLIPSE = "pref_eclipse";
    public static final String PREF_UPDATE = "pref_update";
    public static final String PREF_ADS = "pref_ads";
    public static final String PREF_FIREBASE = "pref_firebase";


    /**
     * Save preference params in String value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @param value value in String of name param
     */
    public static void savePreferString(Context context, String prefName, String name, String value){
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(name, value);
        editor.apply();
    }

    /**
     * Save preference params in Long value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @param value value in Long of name param
     */
    public static void savePreferLong(Context context, String prefName, String name, long value){
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putLong(name, value);
        editor.apply();
    }

    /**
     * Save preference params in Float value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @param value value in Float of name param
     */
    public static void savePreferFloat(Context context, String prefName, String name, float value){
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putFloat(name, value);
        editor.apply();
    }

    /**
     * Save preference params in integer value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @param value value in integer of name param
     */
    public static void savePreferInt(Context context, String prefName, String name, int value){
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(name, value);
        editor.apply();
    }

    /**
     * Save preference params in boolean value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @param value value in boolean of name param
     */
    public static void savePreferBool(Context context, String prefName, String name, boolean value){
        SharedPreferences preferences = context.getSharedPreferences(prefName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(name, value);
        editor.apply();
    }

    /**
     * Get value from shared preference name in integer value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @return return param in integer value, return -1 if no value
     */
    public static int getPreferInt(Context context, String prefName, String name){
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE).getInt(name, -1);
    }

    /**
     * Get value from shared preference name in String value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @return return param in string value, Return "" if no value
     */
    public static String getPreferString(Context context, String prefName, String name){
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE).getString(name, null);
    }

    /**
     * Get value from shared preference name in boolean value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @return return param in boolean value, Return false if no value
     */
    public static boolean getPreferBool(Context context, String prefName, String name){
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE).getBoolean(name, false);
    }

    /**
     * Get value from shared preference name in Long value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @return return param in Long value, Return -1 if no value
     */
    public static long getPreferLong(Context context, String prefName, String name){
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE).getLong(name, -1);
    }

    /**
     * Get value from shared preference name in Float value
     *
     * @param context Application context
     * @param prefName Preference Name
     * @param name Name param
     * @return return param in Float value, Return -1 if no value
     */
    public static float getPreferFloat(Context context, String prefName, String name){
        return context.getSharedPreferences(prefName, Context.MODE_PRIVATE).getFloat(name, -1);
    }

    /**
     * Clear Shared Preference params in Preference Name
     *
     * @param context Application context
     * @param prefName Preference Name
     */
    public static void prefClear(Context context, String prefName) {
        context.getSharedPreferences(prefName, Context.MODE_PRIVATE).edit().clear().apply();
    }
}
