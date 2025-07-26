package com.demo.bluetoothbatterylevel.activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.demo.bluetoothbatterylevel.R;
import com.demo.bluetoothbatterylevel.service.ServiceDebugHelper;
import com.demo.bluetoothbatterylevel.service.UnifiedBluetoothService;


/**
 * Minimal test activity - sadece temel fonksiyonları test etmek için
 * Bu çalışırsa main activity'ye geçeriz
 */
public class MinimalTestActivity extends AppCompatActivity {
    private static final String TAG = "MinimalTestActivity";

    private TextView statusText;
    private Button startServiceButton;
    private Button stopServiceButton;
    private Button checkServiceButton;
    private Button testBatteryButton;
    private Button debugServiceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_minimal_test);

        Log.d(TAG, "🚀 MinimalTestActivity starting...");

        initViews();
        setupButtons();

        // Debug raporu
        new Handler().postDelayed(() -> {
            ServiceDebugHelper.fullDebugReport(this);
        }, 1000);
    }

    private void initViews() {
        statusText = findViewById(R.id.statusText);
        startServiceButton = findViewById(R.id.startServiceButton);
        stopServiceButton = findViewById(R.id.stopServiceButton);
        checkServiceButton = findViewById(R.id.checkServiceButton);
        testBatteryButton = findViewById(R.id.testBatteryButton); // YENİ
        debugServiceButton = findViewById(R.id.debugServiceButton);
        updateStatus("App started - Ready for testing");
    }

    private void setupButtons() {
        startServiceButton.setOnClickListener(v -> {
            Log.d(TAG, "🔧 Start Service button clicked");
            updateStatus("Starting service...");

            try {
                Intent serviceIntent = new Intent(this, UnifiedBluetoothService.class);
                startForegroundService(serviceIntent);

                updateStatus("Service start command sent");

                // 2 saniye sonra kontrol et
                new Handler().postDelayed(() -> {
                    boolean running = ServiceDebugHelper.isServiceRunning(this);
                    updateStatus("Service running: " + running);
                }, 2000);

            } catch (Exception e) {
                Log.e(TAG, "❌ Error starting service", e);
                updateStatus("Error starting service: " + e.getMessage());
            }
        });

        stopServiceButton.setOnClickListener(v -> {
            Log.d(TAG, "⏹️ Stop Service button clicked");
            updateStatus("Stopping service...");

            try {
                Intent serviceIntent = new Intent(this, UnifiedBluetoothService.class);
                stopService(serviceIntent);

                updateStatus("Service stop command sent");

                new Handler().postDelayed(() -> {
                    boolean running = ServiceDebugHelper.isServiceRunning(this);
                    updateStatus("Service running: " + running);
                }, 1000);

            } catch (Exception e) {
                Log.e(TAG, "❌ Error stopping service", e);
                updateStatus("Error stopping service: " + e.getMessage());
            }
        });

        checkServiceButton.setOnClickListener(v -> {
            Log.d(TAG, "🔍 Check Service button clicked");

            boolean running = ServiceDebugHelper.isServiceRunning(this);
            updateStatus("Service currently running: " + running);

            ServiceDebugHelper.fullDebugReport(this);

            // YENI: Bluetooth cihaz durumunu da kontrol et
            checkBluetoothDevices();
        });

        testBatteryButton.setOnClickListener(v -> {
            Log.d(TAG, "🔋 Test Battery Detection button clicked");
            testBatteryDetection();
            Intent testIntent = new Intent("com.demo.bluetoothbatterylevel.TEST_SERVICE");
            testIntent.putExtra("test_device", "Test JBL Headphones");
            testIntent.putExtra("test_battery", "85%");
            sendBroadcast(testIntent);

            Log.d(TAG, "📡 Test service intent sent");
        });
        debugServiceButton.setOnClickListener(v -> {
            Log.d(TAG, "🔧 Debug Service button clicked");

            // Explicit broadcast kullan (daha güvenilir)
            Intent debugIntent = new Intent("com.demo.bluetoothbatterylevel.DEBUG_SERVICE");
            debugIntent.setPackage(getPackageName()); // EKLE: Package belirt
            sendBroadcast(debugIntent);

            updateStatus("Debug command sent - check logs!");
            Log.d(TAG, "📡 Debug intent broadcasted with package: " + getPackageName());
        });
    }

    /**
     * YENI METOD: Bluetooth cihazları kontrol et
     */
    private void checkBluetoothDevices() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

            if (adapter == null) {
                Log.w(TAG, "⚠️ Bluetooth adapter is null");
                updateStatus("Bluetooth not supported");
                return;
            }

            if (!adapter.isEnabled()) {
                Log.w(TAG, "⚠️ Bluetooth is disabled");
                updateStatus("Service running: " + ServiceDebugHelper.isServiceRunning(this) + " | Bluetooth: OFF");
                return;
            }

            Log.d(TAG, "✅ Bluetooth is enabled");

            // Bağlı cihazları kontrol et
            try {
                java.util.Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();
                Log.d(TAG, "📱 Paired devices count: " + bondedDevices.size());

                int connectedCount = 0;
                for (BluetoothDevice device : bondedDevices) {
                    try {
                        boolean isConnected = (Boolean) device.getClass()
                                .getMethod("isConnected")
                                .invoke(device);

                        if (isConnected) {
                            connectedCount++;
                            Log.d(TAG, "🔗 Connected device: " + device.getName() + " (" + device.getBluetoothClass().getDeviceClass() + ")");
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Error checking device connection: " + device.getName());
                    }
                }

                String statusMsg = String.format("Service: %s | BT: ON | Paired: %d | Connected: %d",
                        ServiceDebugHelper.isServiceRunning(this) ? "✅" : "❌",
                        bondedDevices.size(),
                        connectedCount);
                updateStatus(statusMsg);

                if (connectedCount > 0) {
                    Log.d(TAG, "🎧 Found connected devices - service should detect them");
                } else {
                    Log.d(TAG, "📱 No connected devices - connect a headphone to test detection");
                }

            } catch (SecurityException e) {
                Log.e(TAG, "❌ Permission denied for Bluetooth access", e);
                updateStatus("Bluetooth permission denied");
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error checking Bluetooth devices", e);
            updateStatus("Error checking Bluetooth: " + e.getMessage());
        }
    }

    private void updateStatus(String message) {
        runOnUiThread(() -> {
            if (statusText != null) {
                statusText.setText(message);
                Log.d(TAG, "📱 Status: " + message);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "🛑 MinimalTestActivity destroyed");
    }

    private void testBatteryDetection() {
        try {
            BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
            if (adapter == null || !adapter.isEnabled()) {
                updateStatus("Bluetooth not available");
                return;
            }

            updateStatus("Testing battery detection...");

            // Bağlı cihazları test et
            java.util.Set<BluetoothDevice> bondedDevices = adapter.getBondedDevices();

            for (BluetoothDevice device : bondedDevices) {
                try {
                    // Connection check
                    boolean isConnected = (Boolean) device.getClass()
                            .getMethod("isConnected")
                            .invoke(device);

                    if (!isConnected) continue;

                    Log.d(TAG, "🎧 Testing device: " + device.getName() + " (class: " + device.getBluetoothClass().getDeviceClass() + ")");

                    // Battery level test
                    testDeviceBatteryLevel(device);

                } catch (Exception e) {
                    Log.e(TAG, "Error testing device: " + device.getName(), e);
                }
            }

        } catch (Exception e) {
            Log.e(TAG, "❌ Error in battery detection test", e);
            updateStatus("Error testing battery detection");
        }
    }

    private void testDeviceBatteryLevel(BluetoothDevice device) {
        try {
            Log.d(TAG, "🔋 Testing battery for: " + device.getName());

            // Profile proxy ile battery level al
            BluetoothAdapter.getDefaultAdapter().getProfileProxy(this, new android.bluetooth.BluetoothProfile.ServiceListener() {
                @Override
                public void onServiceConnected(int profile, android.bluetooth.BluetoothProfile bluetoothProfile) {
                    try {
                        for (BluetoothDevice connectedDevice : bluetoothProfile.getConnectedDevices()) {
                            if (connectedDevice.getAddress().equals(device.getAddress())) {

                                // Device class check
                                int deviceClass = connectedDevice.getBluetoothClass().getDeviceClass();
                                Log.d(TAG, "📱 Device class: " + deviceClass);

                                if (deviceClass == 1028 || deviceClass == 1048) { // Headphone classes

                                    // Battery level reflection
                                    try {
                                        java.lang.reflect.Method method = connectedDevice.getClass().getMethod("getBatteryLevel");
                                        Integer batteryLevel = (Integer) method.invoke(connectedDevice);

                                        if (batteryLevel != null && batteryLevel != -1) {
                                            String batteryInfo = connectedDevice.getName() + ": " + batteryLevel + "%";
                                            Log.d(TAG, "🔋 BATTERY FOUND: " + batteryInfo);

                                            runOnUiThread(() -> {
                                                updateStatus("BATTERY: " + batteryInfo);
                                            });

                                            // Service'e manuel broadcast gönder
                                            sendManualBatteryBroadcast(connectedDevice.getName(), batteryLevel + "%");

                                        } else {
                                            Log.w(TAG, "⚠️ Battery level not available for " + connectedDevice.getName());
                                            runOnUiThread(() -> {
                                                updateStatus(connectedDevice.getName() + ": Battery not available");
                                            });
                                        }

                                    } catch (Exception e) {
                                        Log.e(TAG, "❌ Error getting battery level", e);
                                        runOnUiThread(() -> {
                                            updateStatus(connectedDevice.getName() + ": Battery error - " + e.getMessage());
                                        });
                                    }

                                } else {
                                    Log.d(TAG, "⚠️ Device is not a headphone (class: " + deviceClass + ")");
                                }
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error in service connected", e);
                    } finally {
                        BluetoothAdapter.getDefaultAdapter().closeProfileProxy(profile, bluetoothProfile);
                    }
                }

                @Override
                public void onServiceDisconnected(int profile) {
                    Log.d(TAG, "Service disconnected: " + profile);
                }
            }, android.bluetooth.BluetoothProfile.HEADSET);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error testing device battery", e);
        }
    }
    private void sendManualBatteryBroadcast(String deviceName, String batteryLevel) {
        try {
            Intent intent = new Intent("com.demo.bluetoothbatterylevel.BATTERY_UPDATE");
            intent.putExtra("manual_test", true);
            intent.putExtra("device_name", deviceName);
            intent.putExtra("battery_level", batteryLevel);
            intent.setPackage(getPackageName());
            sendBroadcast(intent);

            Log.d(TAG, "📡 Manual battery broadcast sent: " + deviceName + " " + batteryLevel);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error sending manual broadcast", e);
        }
    }
}