package com.pongodev.dailyworkout.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;

import com.google.android.gms.ads.AdView;

import java.util.Locale;

/**
 * Design and developed by pongodev.com
 *
 * Utils is created to set application configuration, from database path, ad visibility.
 */
public class Utils {

    // Key values for passing data between activities
    public static final String ARG_PARENT_PAGE          = "parent_page";
    public static final String ARG_WORKOUT_ID           = "workout_id";
    public static final String ARG_WORKOUT_NAME         = "workout_name";
    public static final String ARG_WORKOUT_IDS          = "workout_ids";
    public static final String ARG_WORKOUT_NAMES        = "workout_names";
    public static final String ARG_WORKOUT_IMAGES       = "workout_images";
    public static final String ARG_WORKOUT_TIMES        = "workout_times";
    public static final String ARG_WORKOUTS 	        = "workouts";
    public static final String ARG_PROGRAMS 	        = "programs";
    public static final String ARG_CIRCUITNUMBERS = "circuits";
    public static final String ARG_ROUNDNUMBERS = "rounds";
    // Key values for ad interstitial trigger
    public static final String ARG_TRIGGER      = "trigger";

    // Set TextToSpeech language, you can check other locale language code in
    // http://developer.android.com/reference/java/util/Locale.html
    public static final Locale ARG_LOCALE = Locale.US;

    // Configurable parameters. you can configure these parameter.
    // Set database path. It must be similar with package name.
    public static final String ARG_DATABASE_PATH = "/data/data/com.pongodev.dailyworkout/databases/";

    // Set default break time
    public static final String ARG_DEFAULT_BREAK = "00:10";
    public static final String ARG_DEFAULT_START = "00:10";

    // Set default sound volume
    public static final int ARG_SOUND_VOLUME = 7;

    // For every recipe detail you want to display interstitial ad.
    // 3 means interstitial ad will display after user open detail page three times.
    public static final int ARG_TRIGGER_VALUE = 3;
    // Admob visibility parameter. set true to show admob and false to hide.
    public static final boolean IS_ADMOB_VISIBLE = true;
    // Set value to true if you are still in development process,
    // and false if you are ready to publish the app.
    public static final boolean IS_ADMOB_IN_DEBUG = false;

    // Method to check admob visibility
    public static boolean admobVisibility(AdView ad, boolean isInDebugMode){
        if(isInDebugMode) {
            ad.setVisibility(View.VISIBLE);
            return true;
        }else {
            ad.setVisibility(View.GONE);
            return false;
        }
    }

    // Method to load data that stored in SharedPreferences
    public static int loadPreferences(String param, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        return sharedPreferences.getInt(param, 0);
    }

    // Method to save data to SharedPreferences
    public static void savePreferences(String param, int value, Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences("user_data", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(param, value);
        editor.apply();
    }
}

