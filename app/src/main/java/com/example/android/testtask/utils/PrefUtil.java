package com.example.android.testtask.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class PrefUtil {

    private static final String PREF_FIRST_LAUNCH = "com.example.android.testtask.first_launch";

    public static boolean isFirstLaunch(Context ctx) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        return preferences.getBoolean(PREF_FIRST_LAUNCH, true);
    }
    public static void setFirstLaunch(Context ctx, boolean isFirstLaunch) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(PREF_FIRST_LAUNCH, isFirstLaunch);
        editor.apply();
    }

}
