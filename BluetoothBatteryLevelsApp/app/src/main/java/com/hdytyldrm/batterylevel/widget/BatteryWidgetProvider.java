package com.hdytyldrm.batterylevel.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.hdytyldrm.batterylevel.R;
import com.hdytyldrm.batterylevel.activity.StartActivityYeni;
import com.hdytyldrm.batterylevel.model.BatteryData;
import com.hdytyldrm.batterylevel.service.UnifiedBluetoothService;


/*
public class BatteryWidgetProvider extends AppWidgetProvider {

    // Widget'ın dinleyeceği özel Action'lar
    public static final String ACTION_WIDGET_BATTERY_UPDATE = "com.hdytyldrm.batterylevel.WIDGET_BATTERY_UPDATE";
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
*/
/**
 * Home screen widget for Apple audio devices only
 * Displays battery levels for AirPods, Beats, and other Apple audio devices
 */


/**
 * Home screen widget for Apple audio devices only
 * Displays battery levels for AirPods, Beats, and other Apple audio devices
 */
public class BatteryWidgetProvider extends AppWidgetProvider {
    private static final String TAG = "AppleAudioWidget";

    // Widget actions
    public static final String ACTION_WIDGET_BATTERY_UPDATE = "com.hdytyldrm.batterylevel.WIDGET_BATTERY_UPDATE";
    public static final String EXTRA_BATTERY_DATA = "battery_data";

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.d(TAG, "🔄 Widget update requested for " + appWidgetIds.length + " Apple audio widgets");

        for (int appWidgetId : appWidgetIds) {
            // Show "No Apple Device" initially
            updateAppWidget(context, appWidgetManager, appWidgetId, null);
        }
    }

    // Main widget update logic - Apple devices only
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager, int appWidgetId, BatteryData batteryData) {
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);

        // Set click intent to open main app
        Intent intent = new Intent(context, StartActivityYeni.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        views.setOnClickPendingIntent(R.id.widget_container_main, pendingIntent);

        if (batteryData != null && batteryData.isConnected() && batteryData.isAirPods()) {
            // Apple audio device connected
            views.setViewVisibility(R.id.widget_device_name, View.VISIBLE);
            views.setTextViewText(R.id.widget_device_name, getEnhancedAppleDeviceName(batteryData.getDeviceName()));

            // Show Apple audio device battery layout
            views.setViewVisibility(R.id.widget_airpods_section, View.VISIBLE);
            views.setViewVisibility(R.id.widget_disconnected_section, View.GONE);

            // Update battery levels with enhanced text
            String leftText = context.getResources().getString(R.string.battery_level_left_earbud) + ": " + batteryData.getLeftBattery();
            String rightText = context.getResources().getString(R.string.battery_level_right_earbud) + ": " + batteryData.getRightBattery();
            String caseText = getCaseText(context, batteryData);

            views.setTextViewText(R.id.widget_left_text, leftText);
            views.setTextViewText(R.id.widget_right_text, rightText);
            views.setTextViewText(R.id.widget_case_text, caseText);

            // Show charging indicators if needed
            updateChargingIndicators(views, batteryData);

        } else {
            // No Apple audio device connected
            views.setViewVisibility(R.id.widget_device_name, View.GONE);
            views.setViewVisibility(R.id.widget_airpods_section, View.GONE);
            views.setViewVisibility(R.id.widget_disconnected_section, View.VISIBLE);

            // Update disconnected message for Apple devices
           /* views.setTextViewText(R.id.widget_disconnected_text,
                    context.getResources().getString(R.string.widget_connect_message));*/
        }

        appWidgetManager.updateAppWidget(appWidgetId, views);
        Log.d(TAG, "✅ Apple audio widget " + appWidgetId + " updated");
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent != null && ACTION_WIDGET_BATTERY_UPDATE.equals(intent.getAction())) {
            BatteryData batteryData = intent.getParcelableExtra(EXTRA_BATTERY_DATA);

            // Only update if it's an Apple device or disconnected
            if (batteryData == null || batteryData.isDisconnected() || batteryData.isAirPods()) {
                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
                int[] appWidgetIds = appWidgetManager.getAppWidgetIds(
                        new ComponentName(context, BatteryWidgetProvider.class));

                for (int appWidgetId : appWidgetIds) {
                    updateAppWidget(context, appWidgetManager, appWidgetId, batteryData);
                }

                Log.d(TAG, "📡 Apple audio widgets updated: " +
                        (batteryData != null ? batteryData.toString() : "Disconnected"));
            }
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.d(TAG, "✅ Apple audio widget enabled - first widget added");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.d(TAG, "❌ Apple audio widget disabled - last widget removed");
    }

    // Helper Methods

    /**
     * Enhanced Apple device name for widget display
     */
    private static String getEnhancedAppleDeviceName(String deviceName) {
        if (deviceName == null || deviceName.isEmpty()) {
            return "Apple Audio Device";
        }

        String lowerName = deviceName.toLowerCase();

        // AirPods family
        if (lowerName.contains("airpods")) {
            if (lowerName.contains("pro")) return "AirPods Pro";
            if (lowerName.contains("max")) return "AirPods Max";
            return "AirPods";
        }

        // Beats family
        if (lowerName.contains("beats")) {
            if (lowerName.contains("solo")) return "Beats Solo";
            if (lowerName.contains("studio")) return "Beats Studio";
            if (lowerName.contains("powerbeats")) return "Powerbeats";
            if (lowerName.contains("flex")) return "Beats Flex";
            return "Beats";
        }

        return deviceName;
    }

    /**
     * Get appropriate case text based on device type
     */
    private static String getCaseText(Context context, BatteryData batteryData) {
        String deviceName = batteryData.getDeviceName();

        // AirPods Max doesn't have a case
        if (deviceName != null && deviceName.toLowerCase().contains("max")) {
            return context.getResources().getString(R.string.no_case_available);
        }

        return context.getResources().getString(R.string.battery_level_case) + ": " + batteryData.getCaseBattery();
    }

    /**
     * Update charging indicators in widget
     */
    private static void updateChargingIndicators(RemoteViews views, BatteryData batteryData) {
        // Show charging icons if device is charging
        // This could be enhanced with actual charging indicator icons

        if (batteryData.isLeftCharging()) {
            // Could add charging icon for left earbud
        }

        if (batteryData.isRightCharging()) {
            // Could add charging icon for right earbud
        }

        if (batteryData.isCaseCharging()) {
            // Could add charging icon for case
        }
    }
}