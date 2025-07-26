package com.demo.bluetoothbatterylevel.service;

import android.app.ActivityManager;
import android.content.Context;
import android.util.Log;

public class ServiceDebugHelper {
    private static final String TAG = "ServiceDebugHelper";

    /**
     * Service'in çalışıp çalışmadığını kontrol et
     */
    public static boolean isServiceRunning(Context context) {
        try {
            ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

            for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
                if (UnifiedBluetoothService.class.getName().equals(service.service.getClassName())) {
                    Log.d(TAG, "✅ UnifiedBluetoothService IS RUNNING");
                    Log.d(TAG, "  - Process: " + service.process);
                    Log.d(TAG, "  - PID: " + service.pid);
                    Log.d(TAG, "  - Foreground: " + service.foreground);
                    Log.d(TAG, "  - Started: " + service.started);
                    return true;
                }
            }

            Log.w(TAG, "❌ UnifiedBluetoothService NOT RUNNING");
            return false;

        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking service status", e);
            return false;
        }
    }

    /**
     * Service başlatmayı dene ve sonucu logla
     */
    public static void startServiceWithDebug(Context context) {
        try {
            Log.d(TAG, "🚀 Attempting to start UnifiedBluetoothService...");

            android.content.Intent serviceIntent = new android.content.Intent(context, UnifiedBluetoothService.class);
            context.startForegroundService(serviceIntent);

            Log.d(TAG, "✅ startForegroundService() called successfully");

            // 2 saniye bekle ve kontrol et
            new android.os.Handler().postDelayed(() -> {
                boolean running = isServiceRunning(context);
                Log.d(TAG, "🔍 Service status after 2 seconds: " + (running ? "RUNNING" : "NOT RUNNING"));
            }, 2000);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error starting service", e);
        }
    }

    /**
     * Foreground service notification permission kontrolü
     */
    public static void checkNotificationPermission(Context context) {
        try {
            android.app.NotificationManager notificationManager =
                    (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            boolean notificationsEnabled = notificationManager.areNotificationsEnabled();
            Log.d(TAG, "📱 Notifications enabled: " + notificationsEnabled);

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                android.app.NotificationChannel channel = notificationManager.getNotificationChannel("bluetooth_battery_channel");
                if (channel != null) {
                    Log.d(TAG, "📢 Notification channel exists: " + channel.getName());
                    Log.d(TAG, "  - Importance: " + channel.getImportance());
                } else {
                    Log.w(TAG, "⚠️ Notification channel NOT found");
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking notification permission", e);
        }
    }

    /**
     * Bluetooth permission kontrolü
     */
    public static void checkBluetoothPermissions(Context context) {
        try {
            String[] permissions = {
                    "android.permission.BLUETOOTH",
                    "android.permission.BLUETOOTH_ADMIN",
                    "android.permission.BLUETOOTH_CONNECT",
                    "android.permission.BLUETOOTH_SCAN",
                    "android.permission.ACCESS_FINE_LOCATION"
            };

            Log.d(TAG, "🔐 Checking Bluetooth permissions:");

            for (String permission : permissions) {
                int result = androidx.core.content.ContextCompat.checkSelfPermission(context, permission);
                boolean granted = (result == android.content.pm.PackageManager.PERMISSION_GRANTED);
                Log.d(TAG, "  - " + permission + ": " + (granted ? "✅ GRANTED" : "❌ DENIED"));
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking permissions", e);
        }
    }

    /**
     * Tam debug raporu
     */
    public static void fullDebugReport(Context context) {
        Log.d(TAG, "🔍 ===== FULL DEBUG REPORT =====");

        checkBluetoothPermissions(context);
        checkNotificationPermission(context);
        isServiceRunning(context);

        // Bluetooth adapter durumu
        try {
            android.bluetooth.BluetoothAdapter adapter = android.bluetooth.BluetoothAdapter.getDefaultAdapter();
            if (adapter != null) {
                Log.d(TAG, "📶 Bluetooth adapter: " + (adapter.isEnabled() ? "ENABLED" : "DISABLED"));
            } else {
                Log.w(TAG, "⚠️ Bluetooth adapter is NULL");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking Bluetooth adapter", e);
        }

        Log.d(TAG, "🔍 ===== END DEBUG REPORT =====");
    }
}