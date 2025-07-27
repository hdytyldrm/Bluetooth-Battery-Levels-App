package com.hdytyldrm.batterylevel.utils;

import android.content.Context;

public class BatteryUtils {
    private static final String KEY_SAVING_BATTERY = "saving_battery";

    public static boolean isSavingBattery(Context context) {
        PreferenceManager prefManager = new PreferenceManager(context, PreferenceManager.Settings.FILE);
        return prefManager.getBool(KEY_SAVING_BATTERY, true); // Default: true (battery saving ON)
    }

    public static void setSavingBattery(Context context, boolean enabled) {
        PreferenceManager prefManager = new PreferenceManager(context, PreferenceManager.Settings.FILE);
        prefManager.editBool(KEY_SAVING_BATTERY, enabled);
    }
}