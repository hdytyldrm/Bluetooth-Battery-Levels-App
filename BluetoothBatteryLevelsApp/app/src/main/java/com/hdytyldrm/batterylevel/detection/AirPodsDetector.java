package com.hdytyldrm.batterylevel.detection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;

import com.hdytyldrm.batterylevel.model.BatteryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/*
public class AirPodsDetector extends BaseDetectionStrategy {
    private static final String TAG = "AirPodsDetector";

    // OpenPods constants
  //  private static final long RECENT_BEACONS_MAX_T_NS = 10000000000L; // 10s
    private static final long RECENT_BEACONS_MAX_T_NS = 3000000000L; // 3s

    private static final int AIRPODS_MANUFACTURER = 76;
    private static final int AIRPODS_DATA_LENGTH = 27;
    private static final int MIN_RSSI = -60;

    private BluetoothLeScanner bluetoothScanner;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver bluetoothReceiver;
    private AirPodsScanCallback scanCallback;

    private BatteryData currentStatus;
    private boolean maybeConnected = false;
    private final List<ScanResult> recentBeacons = new ArrayList<>();
    private String lastSentStatus = "";
    public AirPodsDetector(Context context) {
        super(context, "AirPodsDetector");
        initializeBluetoothAdapter();
        setupBluetoothReceiver();
        currentStatus = new BatteryData(); // Disconnected state
    }

    private void initializeBluetoothAdapter() {
        try {
            BluetoothManager btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = btManager.getAdapter();

            if (bluetoothAdapter == null) {
                notifyError("Bluetooth adapter not available", null);
            }
        } catch (Exception e) {
            notifyError("Error initializing bluetooth adapter", e);
        }
    }

    @Override
    public void startDetection() {
        if (!isContextValid()) return;

        try {
            Log.d(TAG, "🔄 Starting AirPods BLE detection");
            isActive = true;

            registerBluetoothReceiver();
            startBLEScanner();

            notifyStatusChange("AirPods detection started");

        } catch (Exception e) {
            isActive = false;
            notifyError("Error starting AirPods detection", e);
        }
    }

    @Override
    public void stopDetection() {
        try {
            Log.d(TAG, "⏹️ Stopping AirPods BLE detection");
            isActive = false;

            stopBLEScanner();
            unregisterBluetoothReceiver();
            currentStatus = new BatteryData(); // Reset to disconnected

            notifyStatusChange("AirPods detection stopped");

        } catch (Exception e) {
            notifyError("Error stopping AirPods detection", e);
        }
    }

    @Override
    public void detectDevice(BluetoothDevice device) {
        // AirPods BLE detection manuel cihaz detection'ı desteklemiyor
        // Sadece BLE beacon'ları dinliyoruz
        Log.d(TAG, "Manual device detection not supported for AirPods, using BLE scanning");
    }

    private void startBLEScanner() {
        try {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Log.w(TAG, "Bluetooth not enabled, cannot start BLE scanner");
                return;
            }

            // Önceki scanner'ı durdur
            stopBLEScanner();

            bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothScanner == null) {
                notifyError("BLE scanner not available", null);
                return;
            }

            // Scan settings (OpenPods'dan)
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    .setReportDelay(1) // DON'T USE 0
                    .build();

            // Scan callback
            scanCallback = new AirPodsScanCallback();

            // Scan filters (OpenPods'dan)
            List<ScanFilter> filters = createScanFilters();

            bluetoothScanner.startScan(filters, scanSettings, scanCallback);
            Log.d(TAG, "✅ BLE scanner started");

        } catch (Exception e) {
            notifyError("Error starting BLE scanner", e);
        }
    }

    private void stopBLEScanner() {
        try {
            if (bluetoothScanner != null && scanCallback != null) {
                bluetoothScanner.stopScan(scanCallback);
                scanCallback = null;
                Log.d(TAG, "✅ BLE scanner stopped");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping BLE scanner", e);
        }
    }

    private List<ScanFilter> createScanFilters() {
        // OpenPods'dan alınan filter mantığı
        byte[] manufacturerData = new byte[AIRPODS_DATA_LENGTH];
        byte[] manufacturerDataMask = new byte[AIRPODS_DATA_LENGTH];

        manufacturerData[0] = 7;
        manufacturerData[1] = 25;

        manufacturerDataMask[0] = -1;
        manufacturerDataMask[1] = -1;

        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setManufacturerData(AIRPODS_MANUFACTURER, manufacturerData, manufacturerDataMask);

        return Collections.singletonList(builder.build());
    }
    private void setupBluetoothReceiver() {
        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (action == null || device == null) {
                    Log.w(TAG, "Received null action or device in bluetoothReceiver: action=" + action + ", device=" + (device != null ? device.getName() : "null"));
                    return;
                }

                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    handleBluetoothStateChanged(state);
                } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    if (isAirPodsDevice(device)) {
                        Log.d(TAG, "🎧 AirPods connected: " + device.getName());
                        maybeConnected = true;
                        notifyDeviceConnected(device);
                    }
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    if (isAirPodsDevice(device)) {
                        Log.d(TAG, "🔌 AirPods disconnected: " + device.getName() + ", Address: " + device.getAddress());
                        maybeConnected = false;
                        currentStatus = new BatteryData(); // Durumu sıfırla
                        notifyDeviceDisconnected(device);
                        notifyBatteryData(currentStatus); // Sıfırlanmış durumu bildir
                    } else {
                        Log.w(TAG, "🔌 Non-AirPods device disconnected: " + device.getName());
                    }
                }
            }
        };
    }
    private void handleBluetoothStateChanged(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(TAG, "📴 Bluetooth turned off");
                maybeConnected = false;
                stopBLEScanner();
                notifyStatusChange("Bluetooth turned off");
                break;

            case BluetoothAdapter.STATE_ON:
                Log.d(TAG, "📶 Bluetooth turned on");
                startBLEScanner();
                notifyStatusChange("Bluetooth turned on, BLE scanning started");
                break;
        }
    }

    private boolean isAirPodsDevice(BluetoothDevice device) {
        if (device == null) return false;

        try {
            // İsim kontrolü - TÜM APPLE CIHAZLARI
            String deviceName = device.getName();
            if (deviceName != null) {
                String lowerName = deviceName.toLowerCase();
                if (lowerName.contains("airpods") ||
                        lowerName.contains("beats") ||
                        lowerName.contains("powerbeats") ||
                        lowerName.contains("beatsx")) {
                    return true;
                }
            }

            // UUID kontrolü (mevcut kod)
            android.os.ParcelUuid[] uuids = device.getUuids();
            if (uuids != null) {
                android.os.ParcelUuid[] AIRPODS_UUIDS = {
                        android.os.ParcelUuid.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a"),
                        android.os.ParcelUuid.fromString("2a72e02b-7b99-778f-014d-ad0b7221ec74")
                };

                for (android.os.ParcelUuid deviceUuid : uuids) {
                    for (android.os.ParcelUuid airpodsUuid : AIRPODS_UUIDS) {
                        if (deviceUuid.equals(airpodsUuid)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking AirPods UUIDs", e);
        }

        return false;
    }
    private void registerBluetoothReceiver() {
        if (!isContextValid()) return;

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(bluetoothReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                context.registerReceiver(bluetoothReceiver, filter);
            }

            Log.d(TAG, "✅ Bluetooth receiver registered");

        } catch (Exception e) {
            notifyError("Error registering bluetooth receiver", e);
        }
    }

    private void unregisterBluetoothReceiver() {
        if (!isContextValid()) return;

        try {
            if (bluetoothReceiver != null) {
                context.unregisterReceiver(bluetoothReceiver);
                Log.d(TAG, "✅ Bluetooth receiver unregistered");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering bluetooth receiver", e);
        }
    }

    // ===== BLE Scan Callback =====
    private class AirPodsScanCallback extends ScanCallback {

       */
/* @Override
        public void onScanResult(int callbackType, ScanResult result) {
            try {
                if (!isAirPodsResult(result)) return;

                Log.d(TAG, "📡 AirPods beacon received, RSSI: " + result.getRssi() + "dB");

                // En güçlü beacon'ı al (OpenPods mantığı)
                ScanResult bestResult = getBestResult(result);
                if (bestResult == null || bestResult.getRssi() < MIN_RSSI) {
                    return;
                }

                // Beacon'ı decode et
               *//*
*/
/* BatteryData batteryData = parseAirPodsBeacon(bestResult);
                if (batteryData != null) {
                    currentStatus = batteryData;
                    notifyBatteryData(batteryData);
                }*//*
*/
/*
                BatteryData batteryData = parseAirPodsBeacon(bestResult);
                if (batteryData != null) {
                    // YENİ KONTROL: Sadece durum değiştiyse haber ver
                    String currentBeaconStatus = batteryData.toString();
                    if (!Objects.equals(lastSentStatus, currentBeaconStatus)) {
                        lastSentStatus = currentBeaconStatus;
                        currentStatus = batteryData;
                        notifyDeviceConnected(result.getDevice());

                        notifyBatteryData(batteryData);
                    }
                }

            } catch (Exception e) {
                Log.e(TAG, "Error processing scan result", e);
            }
        }
*//*

       @Override
       public void onScanResult(int callbackType, ScanResult result) {
           try {
               if (!isAirPodsResult(result)) return;

               Log.d(TAG, "📡 AirPods beacon received, RSSI: " + result.getRssi() + "dB");

               ScanResult bestResult = getBestResult(result);
               if (bestResult == null || bestResult.getRssi() < MIN_RSSI) {
                   return;
               }

               BatteryData batteryData = parseAirPodsBeacon(bestResult);
               if (batteryData != null) {
                   currentStatus = batteryData;
                   notifyBatteryData(batteryData); // ESKİ MANTIK: Her beacon'da gönder
               }

           } catch (Exception e) {
               Log.e(TAG, "Error processing scan result", e);
           }
       }
        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                onScanResult(-1, result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            notifyError("BLE scan failed with error code: " + errorCode, null);
        }
    }

    private boolean isAirPodsResult(ScanResult result) {
        return result != null
                && result.getScanRecord() != null
                && isDataValid(result.getScanRecord().getManufacturerSpecificData(AIRPODS_MANUFACTURER));
    }

    private boolean isDataValid(byte[] data) {
        return data != null && data.length == AIRPODS_DATA_LENGTH;
    }

    private ScanResult getBestResult(ScanResult result) {
        // OpenPods'dan alınan en güçlü beacon algoritması
        recentBeacons.add(result);
        ScanResult strongestBeacon = null;

        for (int i = 0; i < recentBeacons.size(); i++) {
            if (SystemClock.elapsedRealtimeNanos() - recentBeacons.get(i).getTimestampNanos() > RECENT_BEACONS_MAX_T_NS) {
                recentBeacons.remove(i--);
                continue;
            }

            if (strongestBeacon == null || strongestBeacon.getRssi() < recentBeacons.get(i).getRssi()) {
                strongestBeacon = recentBeacons.get(i);
            }
        }

        if (strongestBeacon != null && Objects.equals(strongestBeacon.getDevice().getAddress(), result.getDevice().getAddress())) {
            strongestBeacon = result;
        }

        return strongestBeacon;
    }

    private BatteryData parseAirPodsBeacon(ScanResult result) {
        try {
            byte[] data = result.getScanRecord().getManufacturerSpecificData(AIRPODS_MANUFACTURER);
            if (!isDataValid(data)) return null;

            // Hex string'e çevir (OpenPods mantığı)
            StringBuilder hexString = new StringBuilder();
            for (byte b : data) {
                hexString.append(String.format("%02X", b));
            }

            String status = hexString.toString();
            Log.d(TAG, "📊 Raw beacon data: " + status);

            // OpenPods parsing mantığı
            return parseAirPodsStatus(status, result.getDevice());

        } catch (Exception e) {
            Log.e(TAG, "Error parsing AirPods beacon", e);
            return null;
        }
    }

    private BatteryData parseAirPodsStatus(String status, BluetoothDevice device) {
        try {
            if (status == null || status.length() < 16) return null;

            Log.d(TAG, "🔍 Parsing status: " + status);

            // OpenPods parsing mantığı
            boolean flip = isFlipped(status);
            Log.d(TAG, "🔍 Flip: " + flip);

            int leftStatus = Integer.parseInt("" + status.charAt(flip ? 12 : 13), 16);
            int rightStatus = Integer.parseInt("" + status.charAt(flip ? 13 : 12), 16);
            int caseStatus = Integer.parseInt("" + status.charAt(15), 16);

            Log.d(TAG, "🔍 Raw status values - Left: " + leftStatus + ", Right: " + rightStatus + ", Case: " + caseStatus);

            int chargeStatus = Integer.parseInt("" + status.charAt(14), 16);
            boolean chargeL = (chargeStatus & (flip ? 0b00000010 : 0b00000001)) != 0;
            boolean chargeR = (chargeStatus & (flip ? 0b00000001 : 0b00000010)) != 0;
            boolean chargeCase = (chargeStatus & 0b00000100) != 0;

            int inEarStatus = Integer.parseInt("" + status.charAt(11), 16);
            boolean inEarL = (inEarStatus & (flip ? 0b00001000 : 0b00000010)) != 0;
            boolean inEarR = (inEarStatus & (flip ? 0b00000010 : 0b00001000)) != 0;

            // Battery yüzdelerini hesapla
            String leftBattery = formatBatteryLevel(leftStatus);
            String rightBattery = formatBatteryLevel(rightStatus);
            String caseBattery = formatBatteryLevel(caseStatus);

            // Device name al
            String deviceName = device.getName();
            if (deviceName == null || deviceName.isEmpty()) {
                deviceName = "AirPods";
            }

            Log.d(TAG, String.format("🎧 PARSED FINAL: %s L:%s(%s) R:%s(%s) Case:%s(%s)",
                    deviceName, leftBattery, chargeL ? "⚡" : "",
                    rightBattery, chargeR ? "⚡" : "",
                    caseBattery, chargeCase ? "⚡" : ""));

            return new BatteryData(
                    deviceName,
                    device.getAddress(),
                    leftBattery,
                    rightBattery,
                    caseBattery,
                    chargeL,
                    chargeR,
                    chargeCase,
                    inEarL,
                    inEarR
            );

        } catch (Exception e) {
            Log.e(TAG, "Error parsing AirPods status", e);
            return null;
        }
    }

    private boolean isFlipped(String str) {
        // OpenPods mantığı
        return (Integer.parseInt("" + str.charAt(10), 16) & 0x02) == 0;
    }

    private String formatBatteryLevel(int status) {
        Log.d(TAG, "🔋 formatBatteryLevel called with status: " + status);

        // OpenPods mantığı - DÜZELTME
        if (status == 15) {
            Log.d(TAG, "🔋 Status 15 - returning disconnected");
            return "--";  // Disconnected
        }

        if (status >= 0 && status <= 10) {
            int batteryPercent = status * 10; // DÜZELTME: +5 kaldırıldı

            // %100'den fazla olamaz
            if (batteryPercent > 100) {
                batteryPercent = 100;
            }

            Log.d(TAG, "🔋 Status " + status + " -> " + batteryPercent + "%");
            return batteryPercent + "%";
        }

        Log.d(TAG, "🔋 Status " + status + " - invalid, returning --");
        return "--";
    }

    }
*/

/**
 * Enhanced Apple Audio Device Detector
 * Supports all Apple audio devices including:
 * - AirPods (1st, 2nd, 3rd gen)
 * - AirPods Pro (1st, 2nd gen)
 * - AirPods Max
 * - Powerbeats Pro, Powerbeats 3
 * - Beats X, Beats Flex
 * - Beats Solo 3, Beats Studio 3
 */
public class AirPodsDetector extends BaseDetectionStrategy {
    private static final String TAG = "AppleAudioDetector";

    // BLE scanning constants
    private static final long RECENT_BEACONS_MAX_T_NS = 3000000000L; // 3 seconds
    private static final int APPLE_MANUFACTURER = 76; // Apple Inc.
    private static final int APPLE_DATA_LENGTH = 27;
    private static final int MIN_RSSI = -60;

    // Timeout for disconnect detection
    private static final long DISCONNECT_TIMEOUT = 15000; // 15 seconds
    private Handler disconnectHandler = new Handler();
    private Runnable disconnectRunnable;

    // Bluetooth components
    private BluetoothLeScanner bluetoothScanner;
    private BluetoothAdapter bluetoothAdapter;
    private BroadcastReceiver bluetoothReceiver;
    private AppleDeviceScanCallback scanCallback;

    // State management
    private BatteryData currentStatus;
    private boolean maybeConnected = false;
    private final List<ScanResult> recentBeacons = new ArrayList<>();
    private String lastSentStatus = "";

    // Supported Apple audio device models
    private static final String[] SUPPORTED_APPLE_DEVICES = {
            // AirPods family
            "AirPods", "AirPods Pro", "AirPods Max",

            // Beats family
            "Beats Solo", "Beats Studio", "Powerbeats Pro", "Powerbeats 3",
            "Beats X", "BeatsX", "Beats Flex",

            // Legacy naming
            "Beats Solo 3", "Beats Studio 3", "Beats Solo Pro"
    };

    public AirPodsDetector(Context context) {
        super(context, "AppleAudioDetector");
        initializeBluetoothAdapter();
        setupBluetoothReceiver();
        currentStatus = new BatteryData(); // Disconnected state
    }

    private void initializeBluetoothAdapter() {
        try {
            BluetoothManager btManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = btManager.getAdapter();

            if (bluetoothAdapter == null) {
                notifyError("Bluetooth adapter not available", null);
            }
        } catch (Exception e) {
            notifyError("Error initializing bluetooth adapter", e);
        }
    }

    @Override
    public void startDetection() {
        if (!isContextValid()) return;

        try {
            Log.d(TAG, "🚀 Starting Apple audio device BLE detection");
            isActive = true;

            registerBluetoothReceiver();
            startBLEScanner();

            notifyStatusChange("Apple audio device detection started");

        } catch (Exception e) {
            isActive = false;
            notifyError("Error starting Apple audio device detection", e);
        }
    }

    @Override
    public void stopDetection() {
        try {
            Log.d(TAG, "⏹️ Stopping Apple audio device BLE detection");
            isActive = false;

            stopBLEScanner();
            unregisterBluetoothReceiver();
            cancelDisconnectTimeout();
            currentStatus = new BatteryData(); // Reset to disconnected

            notifyStatusChange("Apple audio device detection stopped");

        } catch (Exception e) {
            notifyError("Error stopping Apple audio device detection", e);
        }
    }

    @Override
    public void detectDevice(BluetoothDevice device) {
        // Apple devices use BLE beacon detection, manual detection not supported
        Log.d(TAG, "Manual device detection not supported for Apple devices, using BLE scanning");
    }

    private void startBLEScanner() {
        try {
            if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
                Log.w(TAG, "Bluetooth not enabled, cannot start BLE scanner");
                return;
            }

            stopBLEScanner(); // Stop any existing scanner

            bluetoothScanner = bluetoothAdapter.getBluetoothLeScanner();
            if (bluetoothScanner == null) {
                notifyError("BLE scanner not available", null);
                return;
            }

            // Optimized scan settings for Apple devices
            ScanSettings scanSettings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_POWER) // Battery efficient
                    .setReportDelay(1) // Minimal delay
                    .build();

            scanCallback = new AppleDeviceScanCallback();
            List<ScanFilter> filters = createAppleScanFilters();

            bluetoothScanner.startScan(filters, scanSettings, scanCallback);
            Log.d(TAG, "✅ BLE scanner started - Mode: LOW_POWER");

        } catch (Exception e) {
            notifyError("Error starting BLE scanner", e);
        }
    }

    private void stopBLEScanner() {
        try {
            if (bluetoothScanner != null && scanCallback != null) {
                bluetoothScanner.stopScan(scanCallback);
                scanCallback = null;
                Log.d(TAG, "✅ BLE scanner stopped");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error stopping BLE scanner", e);
        }
    }

    private List<ScanFilter> createAppleScanFilters() {
        // Enhanced filter for Apple audio devices
        byte[] manufacturerData = new byte[APPLE_DATA_LENGTH];
        byte[] manufacturerDataMask = new byte[APPLE_DATA_LENGTH];

        // Apple audio device signature
        manufacturerData[0] = 7;
        manufacturerData[1] = 25;

        manufacturerDataMask[0] = -1;
        manufacturerDataMask[1] = -1;

        ScanFilter.Builder builder = new ScanFilter.Builder();
        builder.setManufacturerData(APPLE_MANUFACTURER, manufacturerData, manufacturerDataMask);

        return Collections.singletonList(builder.build());
    }

    private void setupBluetoothReceiver() {
        bluetoothReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);

                if (action == null || device == null) {
                    Log.w(TAG, "Received null action or device in bluetoothReceiver");
                    return;
                }

                if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)) {
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    handleBluetoothStateChanged(state);
                } else if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
                    if (isAppleAudioDevice(device)) {
                        Log.d(TAG, "🎧 Apple audio device connected: " + device.getName());
                        maybeConnected = true;
                        notifyDeviceConnected(device);
                    }
                } else if (BluetoothDevice.ACTION_ACL_DISCONNECTED.equals(action)) {
                    if (isAppleAudioDevice(device)) {
                        Log.d(TAG, "🔌 Apple audio device disconnected: " + device.getName());
                        maybeConnected = false;
                        currentStatus = new BatteryData();
                        notifyDeviceDisconnected(device);
                        notifyBatteryData(currentStatus);
                    }
                }
            }
        };
    }

    private void handleBluetoothStateChanged(int state) {
        switch (state) {
            case BluetoothAdapter.STATE_OFF:
            case BluetoothAdapter.STATE_TURNING_OFF:
                Log.d(TAG, "📴 Bluetooth turned off");
                maybeConnected = false;
                stopBLEScanner();
                notifyStatusChange("Bluetooth turned off");
                break;

            case BluetoothAdapter.STATE_ON:
                Log.d(TAG, "📶 Bluetooth turned on");
                startBLEScanner();
                notifyStatusChange("Bluetooth turned on, BLE scanning started");
                break;
        }
    }

    /**
     * Enhanced Apple audio device detection
     * Supports all Apple audio device models
     */
    private boolean isAppleAudioDevice(BluetoothDevice device) {
        if (device == null) return false;

        try {
            // Device name check - ALL APPLE AUDIO DEVICES
            String deviceName = device.getName();
            if (deviceName != null) {
                String lowerName = deviceName.toLowerCase();

                // Check against supported device list
                for (String supportedDevice : SUPPORTED_APPLE_DEVICES) {
                    if (lowerName.contains(supportedDevice.toLowerCase())) {
                        return true;
                    }
                }

                // Additional generic checks
                if (lowerName.contains("beats") || lowerName.contains("airpods")) {
                    return true;
                }
            }

            // UUID check for Apple devices
            android.os.ParcelUuid[] uuids = device.getUuids();
            if (uuids != null) {
                android.os.ParcelUuid[] APPLE_AUDIO_UUIDS = {
                        android.os.ParcelUuid.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a"),
                        android.os.ParcelUuid.fromString("2a72e02b-7b99-778f-014d-ad0b7221ec74")
                };

                for (android.os.ParcelUuid deviceUuid : uuids) {
                    for (android.os.ParcelUuid appleUuid : APPLE_AUDIO_UUIDS) {
                        if (deviceUuid.equals(appleUuid)) {
                            return true;
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking Apple audio device UUIDs", e);
        }

        return false;
    }

    private void registerBluetoothReceiver() {
        if (!isContextValid()) return;

        try {
            IntentFilter filter = new IntentFilter();
            filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
            filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
            filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.registerReceiver(bluetoothReceiver, filter, Context.RECEIVER_NOT_EXPORTED);
            } else {
                context.registerReceiver(bluetoothReceiver, filter);
            }

            Log.d(TAG, "✅ Bluetooth receiver registered");

        } catch (Exception e) {
            notifyError("Error registering bluetooth receiver", e);
        }
    }

    private void unregisterBluetoothReceiver() {
        if (!isContextValid()) return;

        try {
            if (bluetoothReceiver != null) {
                context.unregisterReceiver(bluetoothReceiver);
                Log.d(TAG, "✅ Bluetooth receiver unregistered");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error unregistering bluetooth receiver", e);
        }
    }

    // ===== BLE Scan Callback =====
    private class AppleDeviceScanCallback extends ScanCallback {

        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            try {
                if (!isAppleDeviceResult(result)) return;

                Log.d(TAG, "📡 Apple audio device beacon received, RSSI: " + result.getRssi() + "dB");

                ScanResult bestResult = getBestResult(result);
                if (bestResult == null || bestResult.getRssi() < MIN_RSSI) {
                    return;
                }

                BatteryData batteryData = parseAppleDeviceBeacon(bestResult);
                if (batteryData != null) {
                    currentStatus = batteryData;
                    notifyBatteryData(batteryData);

                    // Start disconnect timeout
                    startDisconnectTimeout();
                }

            } catch (Exception e) {
                Log.e(TAG, "Error processing scan result", e);
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult result : results) {
                onScanResult(-1, result);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            notifyError("BLE scan failed with error code: " + errorCode, null);
        }
    }

    private boolean isAppleDeviceResult(ScanResult result) {
        return result != null
                && result.getScanRecord() != null
                && isDataValid(result.getScanRecord().getManufacturerSpecificData(APPLE_MANUFACTURER));
    }

    private boolean isDataValid(byte[] data) {
        return data != null && data.length == APPLE_DATA_LENGTH;
    }

    private ScanResult getBestResult(ScanResult result) {
        // Get strongest beacon from recent results
        recentBeacons.add(result);
        ScanResult strongestBeacon = null;

        for (int i = 0; i < recentBeacons.size(); i++) {
            if (SystemClock.elapsedRealtimeNanos() - recentBeacons.get(i).getTimestampNanos() > RECENT_BEACONS_MAX_T_NS) {
                recentBeacons.remove(i--);
                continue;
            }

            if (strongestBeacon == null || strongestBeacon.getRssi() < recentBeacons.get(i).getRssi()) {
                strongestBeacon = recentBeacons.get(i);
            }
        }

        if (strongestBeacon != null && Objects.equals(strongestBeacon.getDevice().getAddress(), result.getDevice().getAddress())) {
            strongestBeacon = result;
        }

        return strongestBeacon;
    }

    private BatteryData parseAppleDeviceBeacon(ScanResult result) {
        try {
            byte[] data = result.getScanRecord().getManufacturerSpecificData(APPLE_MANUFACTURER);
            if (!isDataValid(data)) return null;

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : data) {
                hexString.append(String.format("%02X", b));
            }

            String status = hexString.toString();
            Log.d(TAG, "📊 Raw beacon data: " + status);

            return parseAppleDeviceStatus(status, result.getDevice());

        } catch (Exception e) {
            Log.e(TAG, "Error parsing Apple device beacon", e);
            return null;
        }
    }

    private BatteryData parseAppleDeviceStatus(String status, BluetoothDevice device) {
        try {
            if (status == null || status.length() < 16) return null;

            Log.d(TAG, "🔍 Parsing status: " + status);

            // Parse using Apple beacon format
            boolean flip = isFlipped(status);
            Log.d(TAG, "🔍 Flip: " + flip);

            int leftStatus = Integer.parseInt("" + status.charAt(flip ? 12 : 13), 16);
            int rightStatus = Integer.parseInt("" + status.charAt(flip ? 13 : 12), 16);
            int caseStatus = Integer.parseInt("" + status.charAt(15), 16);

            Log.d(TAG, "🔍 Raw status values - Left: " + leftStatus + ", Right: " + rightStatus + ", Case: " + caseStatus);

            int chargeStatus = Integer.parseInt("" + status.charAt(14), 16);
            boolean chargeL = (chargeStatus & (flip ? 0b00000010 : 0b00000001)) != 0;
            boolean chargeR = (chargeStatus & (flip ? 0b00000001 : 0b00000010)) != 0;
            boolean chargeCase = (chargeStatus & 0b00000100) != 0;

            int inEarStatus = Integer.parseInt("" + status.charAt(11), 16);
            boolean inEarL = (inEarStatus & (flip ? 0b00001000 : 0b00000010)) != 0;
            boolean inEarR = (inEarStatus & (flip ? 0b00000010 : 0b00001000)) != 0;

            // Calculate battery percentages
            String leftBattery = formatBatteryLevel(leftStatus);
            String rightBattery = formatBatteryLevel(rightStatus);
            String caseBattery = formatBatteryLevel(caseStatus);

            // DÜZELTME: Beacon verisinden cihaz tipini tespit et
            String deviceName = detectDeviceTypeFromBeacon(status, device);

            Log.d(TAG, String.format("🎧 PARSED FINAL: %s L:%s(%s) R:%s(%s) Case:%s(%s)",
                    deviceName, leftBattery, chargeL ? "⚡" : "",
                    rightBattery, chargeR ? "⚡" : "",
                    caseBattery, chargeCase ? "⚡" : ""));

            return new BatteryData(
                    deviceName,
                    device.getAddress(),
                    leftBattery,
                    rightBattery,
                    caseBattery,
                    chargeL,
                    chargeR,
                    chargeCase,
                    inEarL,
                    inEarR
            );

        } catch (Exception e) {
            Log.e(TAG, "Error parsing Apple device status", e);
            return null;
        }
    }
    private boolean isFlipped(String str) {
        return (Integer.parseInt("" + str.charAt(10), 16) & 0x02) == 0;
    }

    private String formatBatteryLevel(int status) {
        Log.d(TAG, "🔋 formatBatteryLevel called with status: " + status);

        if (status == 15) {
            Log.d(TAG, "🔋 Status 15 - returning disconnected");
            return "--";
        }

        if (status >= 0 && status <= 10) {
            int batteryPercent = status * 10;
            if (batteryPercent > 100) {
                batteryPercent = 100;
            }

            Log.d(TAG, "🔋 Status " + status + " -> " + batteryPercent + "%");
            return batteryPercent + "%";
        }

        Log.d(TAG, "🔋 Status " + status + " - invalid, returning --");
        return "--";
    }

    /**
     * Get proper Apple device name with model detection
     */
    private String getAppleDeviceName(BluetoothDevice device) {
        String deviceName = device.getName();
        if (deviceName == null || deviceName.isEmpty()) {
            return "Apple Audio Device";
        }

        // Enhance name for specific models
        String lowerName = deviceName.toLowerCase();

        // AirPods model detection
        if (lowerName.contains("airpods")) {
            if (lowerName.contains("pro")) return "AirPods Pro";
            if (lowerName.contains("max")) return "AirPods Max";
            return "AirPods";
        }

        // Beats model detection
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
     * Start disconnect timeout - if no beacons received, assume disconnected
     */
    private void startDisconnectTimeout() {
        cancelDisconnectTimeout();

        disconnectRunnable = () -> {
            Log.d(TAG, "🔌 Apple device beacon timeout - assuming disconnected");
            // DÜZELTME: Service'e açıkça disconnect event gönder
            notifyDeviceDisconnected(null); // Null device ile disconnect
            notifyBatteryData(new BatteryData()); // Boş data gönder
        };

        disconnectHandler.postDelayed(disconnectRunnable, DISCONNECT_TIMEOUT);
        Log.d(TAG, "⏰ Disconnect timeout started: " + DISCONNECT_TIMEOUT + "ms");
    }

    private void cancelDisconnectTimeout() {
        if (disconnectRunnable != null) {
            disconnectHandler.removeCallbacks(disconnectRunnable);
            disconnectRunnable = null;
        }
    }

    /**
     * Beacon verisinden Apple cihaz tipini tespit eder
     */
    private String detectDeviceTypeFromBeacon(String status, BluetoothDevice device) {
        try {
            // Önce Bluetooth device name'i dene
            String bluetoothName = device.getName();
            if (bluetoothName != null && !bluetoothName.isEmpty()) {
                Log.d(TAG, "🎯 Bluetooth name available: " + bluetoothName);
                return enhanceAppleDeviceName(bluetoothName);
            }

            // Beacon verisinden cihaz tipini tespit et
            if (status.length() >= 54) {
                // Model ID'yi çıkar (hex positions based on Apple's beacon format)
                String modelBytes = status.substring(6, 10); // Model identifier bytes
                Log.d(TAG, "🔍 Model bytes from beacon: " + modelBytes);

                return detectModelFromBeaconBytes(modelBytes);
            }

            // Fallback: Generic Apple Audio Device
            return "AirPods";

        } catch (Exception e) {
            Log.e(TAG, "Error detecting device type from beacon", e);
            return "Apple Audio Device";
        }
    }

    /**
     * Beacon byte'larından model tespit et
     */
    private String detectModelFromBeaconBytes(String modelBytes) {
        // Apple'ın bilinen model ID'leri (hex format)
        switch (modelBytes.toUpperCase()) {
            case "0220": return "AirPods (1st gen)";
            case "0F20": return "AirPods (2nd gen)";
            case "1320": return "AirPods (3rd gen)";
            case "0E20": return "AirPods Pro (1st gen)";
            case "1420": return "AirPods Pro (2nd gen)";
            case "0A20": return "AirPods Max";

            // Beats models
            case "1020": return "Powerbeats Pro";
            case "0B20": return "Powerbeats 3";
            case "0520": return "BeatsX";
            case "1820": return "Beats Flex";
            case "0620": return "Beats Solo 3";
            case "1720": return "Beats Studio 3";

            default:
                Log.d(TAG, "🔍 Unknown model bytes: " + modelBytes + " - defaulting to AirPods");
                return "AirPods";
        }
    }

    /**
     * Apple cihaz adını geliştirir
     */
    private String enhanceAppleDeviceName(String deviceName) {
        if (deviceName == null || deviceName.isEmpty()) {
            return "Apple Audio Device";
        }

        String lowerName = deviceName.toLowerCase();

        // AirPods model detection
        if (lowerName.contains("airpods")) {
            if (lowerName.contains("pro") && lowerName.contains("2")) return "AirPods Pro (2nd gen)";
            if (lowerName.contains("pro")) return "AirPods Pro";
            if (lowerName.contains("max")) return "AirPods Max";
            if (lowerName.contains("3")) return "AirPods (3rd gen)";
            if (lowerName.contains("2")) return "AirPods (2nd gen)";
            return "AirPods";
        }

        // Beats model detection
        if (lowerName.contains("beats")) {
            if (lowerName.contains("solo")) return "Beats Solo";
            if (lowerName.contains("studio")) return "Beats Studio";
            if (lowerName.contains("powerbeats")) return "Powerbeats";
            if (lowerName.contains("flex")) return "Beats Flex";
            return "Beats";
        }

        return deviceName;
    }
}