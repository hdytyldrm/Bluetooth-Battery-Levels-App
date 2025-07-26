package com.demo.bluetoothbatterylevel.detection;

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
import android.os.SystemClock;
import android.util.Log;

import com.demo.bluetoothbatterylevel.model.BatteryData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
               *//* BatteryData batteryData = parseAirPodsBeacon(bestResult);
                if (batteryData != null) {
                    currentStatus = batteryData;
                    notifyBatteryData(batteryData);
                }*//*
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
*/
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