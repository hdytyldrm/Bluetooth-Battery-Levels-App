package com.demo.bluetoothbatterylevel.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Common {
    public static final String ACTION_BATTERY_LEVEL_CHANGED = "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED";
    public static final String EXTRA_BATTERY_LEVEL = "android.bluetooth.device.extra.BATTERY_LEVEL";

    public static String getCurrentDateWithPatten(String str) {
        return new SimpleDateFormat(str, Locale.getDefault()).format(Calendar.getInstance().getTime());
    }
}
