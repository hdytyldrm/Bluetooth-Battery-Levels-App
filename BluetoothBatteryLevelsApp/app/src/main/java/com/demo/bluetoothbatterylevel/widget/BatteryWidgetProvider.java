package com.demo.bluetoothbatterylevel.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.demo.bluetoothbatterylevel.R;
import com.demo.bluetoothbatterylevel.activity.StartActivityYeni;
import com.demo.bluetoothbatterylevel.model.BatteryData;
import com.demo.bluetoothbatterylevel.service.UnifiedBluetoothService;

/**
 * Home screen widget for displaying battery levels
 * Shows different layouts for AirPods vs Generic devices
 */
/*
public class BatteryWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "BatteryWidgetProvider";

    // Widget actions
    public static final String ACTION_UPDATE_WIDGET = "com.demo.bluetoothbatterylevel.UPDATE_WIDGET";
    public static final String EXTRA_WIDGET_BATTERY_DATA = "widget_battery_data";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "🔄 Widget update requested for " + appWidgetIds.length + " widgets");

        // Update tüm widget'lar için
        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, null);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        String action = intent.getAction();
        Log.d(TAG, "📡 Widget received action: " + action);

        if (ACTION_UPDATE_WIDGET.equals(action)) {
            // Service'dan battery data güncelleme
            BatteryData batteryData = intent.getParcelableExtra(EXTRA_WIDGET_BATTERY_DATA);
            updateAllWidgets(context, batteryData);

        } else if (UnifiedBluetoothService.ACTION_BATTERY_UPDATE.equals(action)) {
            // Direct service broadcast'i dinle
            BatteryData batteryData = intent.getParcelableExtra(UnifiedBluetoothService.EXTRA_BATTERY_DATA);
            updateAllWidgets(context, batteryData);
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "✅ Widget enabled - first widget added to home screen");

        // İlk widget eklendiğinde service'i başlat
        startMonitoringService(context);
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "❌ Widget disabled - last widget removed from home screen");

        // TODO: Optionally stop service when no widgets remain
        // stopMonitoringService(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.d(TAG, "🗑️ Widgets deleted: " + appWidgetIds.length);
    }

    */
/**
     * Tüm widget'ları güncelle
     *//*

    private void updateAllWidgets(Context context, BatteryData batteryData) {
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                new android.content.ComponentName(context, BatteryWidgetProvider.class)
        );

        Log.d(TAG, "🔄 Updating " + appWidgetIds.length + " widgets with new battery data");

        for (int appWidgetId : appWidgetIds) {
            updateWidget(context, appWidgetManager, appWidgetId, batteryData);
        }
    }

    */
/**
     * Tek bir widget'ı güncelle
     *//*

    private void updateWidget(Context context, AppWidgetManager appWidgetManager,
                              int appWidgetId, BatteryData batteryData) {
        try {
            RemoteViews views;

            if (batteryData == null || batteryData.isDisconnected()) {
                views = createDisconnectedWidget(context);
            } else if (batteryData.isAirPods()) {
                views = createAirPodsWidget(context, batteryData);
            } else if (batteryData.isGeneric()) {
                views = createGenericWidget(context, batteryData);
            } else {
                views = createDisconnectedWidget(context);
            }

            // Click intent - ana uygulamayı aç
            Intent intent = new Intent(context, StartActivityYeni.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    context, 0, intent, PendingIntent.FLAG_IMMUTABLE
            );
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

            // Widget'ı güncelle
            appWidgetManager.updateAppWidget(appWidgetId, views);

            Log.d(TAG, "✅ Widget " + appWidgetId + " updated successfully");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating widget " + appWidgetId, e);
        }
    }

    */
/**
     * Disconnected durumu için widget
     *//*

    private RemoteViews createDisconnectedWidget(Context context) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_disconnected);

        views.setTextViewText(R.id.widget_title, "No Device");
        views.setTextViewText(R.id.widget_subtitle, "Connect headphones");
        views.setImageViewResource(R.id.widget_icon, R.drawable.bluetooth); // mevcut bluetooth icon

        return views;
    }

    */
/**
     * AirPods için widget
     *//*

    private RemoteViews createAirPodsWidget(Context context, BatteryData batteryData) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_airpods);

        // Device name
        views.setTextViewText(R.id.widget_device_name,
                truncateDeviceName(batteryData.getDeviceName()));

        // Battery levels
        views.setTextViewText(R.id.widget_left_battery,
                "L: " + batteryData.getLeftBattery());
        views.setTextViewText(R.id.widget_right_battery,
                "R: " + batteryData.getRightBattery());
        views.setTextViewText(R.id.widget_case_battery,
                "Case: " + batteryData.getCaseBattery());

        // Charging indicators - sistem battery icon kullan
        views.setImageViewResource(R.id.widget_left_icon,
                batteryData.isLeftCharging() ?
                        android.R.drawable.ic_lock_power_off : android.R.drawable.ic_lock_power_off);
        views.setImageViewResource(R.id.widget_right_icon,
                batteryData.isRightCharging() ?
                        android.R.drawable.ic_lock_power_off : android.R.drawable.ic_lock_power_off);
        views.setImageViewResource(R.id.widget_case_icon,
                batteryData.isCaseCharging() ?
                        android.R.drawable.ic_lock_power_off : android.R.drawable.ic_lock_power_off);

        return views;
    }

    */
/**
     * Generic device için widget
     *//*

    private RemoteViews createGenericWidget(Context context, BatteryData batteryData) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_generic);

        // Device name
        views.setTextViewText(R.id.widget_device_name,
                truncateDeviceName(batteryData.getDeviceName()));

        // Battery level
        views.setTextViewText(R.id.widget_battery_level,
                batteryData.getSingleBattery());

        // Charging indicator - sistem battery icon kullan
        views.setImageViewResource(R.id.widget_battery_icon,
                batteryData.isSingleCharging() ?
                        android.R.drawable.ic_lock_power_off : android.R.drawable.ic_lock_power_off);

        // Device icon - mevcut headphone icon kullan
        views.setImageViewResource(R.id.widget_device_icon, R.drawable.headphone);

        return views;
    }

    */
/**
     * Device ismini widget için kısalt
     *//*

    private String truncateDeviceName(String deviceName) {
        if (deviceName == null) return "Unknown";
        if (deviceName.length() > 15) {
            return deviceName.substring(0, 12) + "...";
        }
        return deviceName;
    }

    */
/**
     * Monitoring service'i başlat
     *//*

    private void startMonitoringService(Context context) {
        try {
            Intent serviceIntent = new Intent(context, UnifiedBluetoothService.class);
            context.startForegroundService(serviceIntent);
            Log.d(TAG, "✅ Monitoring service started from widget");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting service from widget", e);
        }
    }

    */
/**
     * Static method - Service'dan widget update için
     *//*

    public static void updateWidgets(Context context, BatteryData batteryData) {
        try {
            Intent intent = new Intent(context, BatteryWidgetProvider.class);
            intent.setAction(ACTION_UPDATE_WIDGET);
            intent.putExtra(EXTRA_WIDGET_BATTERY_DATA, batteryData);
            context.sendBroadcast(intent);

            Log.d(TAG, "📡 Widget update broadcast sent");

        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending widget update", e);
        }
    }
}*/
public class BatteryWidgetProvider extends AppWidgetProvider {

    // Widget'ın dinleyeceği özel Action'lar
    public static final String ACTION_WIDGET_BATTERY_UPDATE = "com.demo.bluetoothbatterylevel.WIDGET_BATTERY_UPDATE";
    public static final String EXTRA_BATTERY_DATA = "battery_data";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Widget eklendiğinde veya belirli aralıklarla sistem tarafından çağrılır.
        for (int appWidgetId : appWidgetIds) {
            // Başlangıçta "Bağlantı Yok" durumunu göster
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }

    // Gerçek widget güncelleme mantığı
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, BatteryData batteryData) {

        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        Intent intent = new Intent(context, StartActivityYeni.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_container_main, pendingIntent);

        if (batteryData != null && batteryData.isConnected()) {
            // Cihaz adını göster ve doldur
            views.setViewVisibility(R.id.widget_device_name, View.VISIBLE);
            views.setTextViewText(R.id.widget_device_name, batteryData.getDeviceName());

            if (batteryData.isAirPods()) {
                // AirPods durumunu göster
                views.setViewVisibility(R.id.widget_airpods_section, View.VISIBLE);
                views.setViewVisibility(R.id.widget_generic_section, View.GONE);
                views.setViewVisibility(R.id.widget_disconnected_section, View.GONE);

                views.setTextViewText(R.id.widget_left_text, context.getResources().getString(R.string.battery_level_left_earbud)+ ": " + batteryData.getLeftBattery());
                views.setTextViewText(R.id.widget_right_text, context.getResources().getString(R.string.battery_level_right_earbud) +": "+ batteryData.getRightBattery());
                views.setTextViewText(R.id.widget_case_text, context.getResources().getString(R.string.battery_level_case)+": "+ batteryData.getCaseBattery());
            } else {
                // Genel cihaz durumunu göster
                views.setViewVisibility(R.id.widget_airpods_section, View.GONE);
                views.setViewVisibility(R.id.widget_generic_section, View.VISIBLE);
                views.setViewVisibility(R.id.widget_disconnected_section, View.GONE);

                views.setTextViewText(R.id.widget_generic_text, batteryData.getSingleBattery());
            }
        } else {
            // Bağlantı yok durumunu göster
            views.setViewVisibility(R.id.widget_device_name, View.GONE); // Cihaz adını gizle
            views.setViewVisibility(R.id.widget_airpods_section, View.GONE);
            views.setViewVisibility(R.id.widget_generic_section, View.GONE);
            views.setViewVisibility(R.id.widget_disconnected_section, View.VISIBLE);
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }
    // Servisten veya başka bir yerden gelen broadcast'i al
    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        // Bu broadcast, bizim widget için özel olarak gönderdiğimiz yayın mı?
        if (intent != null && ACTION_WIDGET_BATTERY_UPDATE.equals(intent.getAction())) {
            BatteryData batteryData = intent.getParcelableExtra(EXTRA_BATTERY_DATA);
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, BatteryWidgetProvider.class));

            for (int appWidgetId : appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId, batteryData);
            }
        }
    }
}
