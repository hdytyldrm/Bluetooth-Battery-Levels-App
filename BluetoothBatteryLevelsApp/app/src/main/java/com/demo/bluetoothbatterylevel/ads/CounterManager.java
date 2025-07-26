package com.demo.bluetoothbatterylevel.ads;

import android.content.Context;
import android.util.Log;

import com.demo.bluetoothbatterylevel.utils.PreferenceManager;


public class CounterManager {
    private final PreferenceManager pm;
    private static final String TAG = "CounterManager";
    private static final int MAX_COUNTER_VALUE = Integer.MAX_VALUE;
    private static CounterManager instance;

    public enum CounterType {
        DIALOG("dialog_counter"),
        SWITCH("switch_counter"),
        ACTIVITY("activity_counter"),
        TOTAL_EVENT("total_event_counter"),
        REWARDED_AD("rewarded_ad_counter");

        public final String key;
        CounterType(String key) {
            this.key = key;
        }
    }

    private CounterManager(Context context) {
        this.pm = new PreferenceManager(context,PreferenceManager.Settings.FILE);
    }

    public static synchronized CounterManager getInstance(Context context) {
        if (instance == null) {
            instance = new CounterManager(context.getApplicationContext());
        }
        return instance;
    }

    public void incrementCounter(CounterType counterType, int incrementBy) {
        if (incrementBy < 0) {
            Log.w(TAG, "incrementBy negatif olamaz: " + incrementBy);
            return;
        }
        int counter = pm.getInt(counterType.key, 0);
        counter += incrementBy;
        if (counter > MAX_COUNTER_VALUE) {
            counter = 0;
            Log.w(TAG, counterType.key + " maksimum değere ulaştı, sıfırlandı.");
        }
        pm.editInt(counterType.key, counter);
        Log.d(TAG, counterType.key + " artırıldı: " + counter);
    }

    public void incrementCounter(CounterType counterType) {
        incrementCounter(counterType, 1);
    }

    public int getCounter(CounterType counterType) {
        return pm.getInt(counterType.key, 0);
    }

    public boolean shouldShowBasedOnCounter(CounterType counterType, int threshold, boolean autoReset) {
        if (threshold <= 0) {
            Log.w(TAG, "Threshold değeri geçersiz: " + threshold);
            return false;
        }
        int counter = getCounter(counterType);
        boolean shouldShow = counter >= threshold;
        Log.d(TAG, counterType.key + " kontrol edildi. Değer: " + counter + ", Eşik: " + threshold + ", Gösterim: " + shouldShow);
        if (shouldShow && autoReset) {
            resetCounter(counterType);
            Log.d(TAG, counterType.key + " eşiğe ulaşıldı ve sıfırlandı.");
        }
        return shouldShow;
    }

    public boolean shouldShowBasedOnCounter(CounterType counterType, int threshold) {
        return shouldShowBasedOnCounter(counterType, threshold, true);
    }

    public boolean shouldShowEveryNCount(CounterType counterType, int threshold) {
        if (threshold <= 0) {
            Log.w(TAG, "Threshold değeri geçersiz: " + threshold);
            return false;
        }
        int counter = getCounter(counterType);
        boolean shouldShow = counter > 0 && counter % threshold == 0;
        Log.d(TAG, counterType.key + " kontrol edildi. Değer: " + counter + ", Eşik: " + threshold + ", Gösterim: " + shouldShow);
        return shouldShow;
    }

    public void resetCounter(CounterType counterType) {
        pm.editInt(counterType.key, 0);
        Log.d(TAG, counterType.key + " sıfırlandı.");
    }

    public void resetAllCounters() {
        for (CounterType type : CounterType.values()) {
            resetCounter(type);
        }
        Log.d(TAG, "Tüm sayaçlar sıfırlandı.");
    }

    public boolean isCounterMaxed(CounterType counterType) {
        return getCounter(counterType) >= MAX_COUNTER_VALUE;
    }
}