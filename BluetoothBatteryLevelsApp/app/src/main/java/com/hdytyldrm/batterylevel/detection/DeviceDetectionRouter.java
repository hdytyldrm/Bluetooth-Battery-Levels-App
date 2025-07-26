package com.hdytyldrm.batterylevel.detection;


import android.bluetooth.BluetoothDevice;
import android.os.ParcelUuid;
import android.util.Log;
import com.hdytyldrm.batterylevel.model.DeviceDetectionResult;
import com.hdytyldrm.batterylevel.model.DeviceType;

public class DeviceDetectionRouter {
    private static final String TAG = "DeviceDetectionRouter";

    // AirPods UUID'ları (OpenPods'dan alındı)
    private static final ParcelUuid[] AIRPODS_UUIDS = {
            ParcelUuid.fromString("74ec2172-0bad-4d01-8f77-997b2be0722a"),
            ParcelUuid.fromString("2a72e02b-7b99-778f-014d-ad0b7221ec74")
    };

    // AirPods ve Beats model isimleri
    private static final String[] AIRPODS_NAMES = {
            "AirPods", "Beats", "Powerbeats", "BeatsX", "Beats Solo", "Beats Studio"
    };

    // Kulaklık device class'ları (mevcut uygulamadan)
    private static final int DEVICE_CLASS_HEADPHONES = 1048;
    private static final int DEVICE_CLASS_HEADSET = 1028;

    /**
     * Ana detection metodu - cihaz tipini belirler
     */
    public static DeviceDetectionResult detectDeviceType(BluetoothDevice device) {
        if (device == null) {
            return new DeviceDetectionResult(null, DeviceType.DISCONNECTED,
                    "Device is null", false);
        }

        try {
            // 1. UUID kontrolü (en güvenilir)
            if (hasAirPodsUUID(device)) {
                Log.d(TAG, "✅ Device detected as AirPods via UUID: " + device.getName());
                return new DeviceDetectionResult(device, DeviceType.AIRPODS,
                        "UUID match", true);
            }

            // 2. İsim kontrolü
            String deviceName = getDeviceName(device);
            if (isAirPodsName(deviceName)) {
                Log.d(TAG, "✅ Device detected as AirPods via name: " + deviceName);
                return new DeviceDetectionResult(device, DeviceType.AIRPODS,
                        "Name match: " + deviceName, true);
            }

            // 3. Device class kontrolü
            int deviceClass = device.getBluetoothClass().getDeviceClass();
            if (isHeadphoneClass(deviceClass)) {
                Log.d(TAG, "✅ Device detected as Generic headphone: " + deviceName +
                        " (class: " + deviceClass + ")");
                return new DeviceDetectionResult(device, DeviceType.GENERIC,
                        "Headphone class: " + deviceClass, true);
            }

            // 4. Bilinmeyen cihaz
            Log.w(TAG, "⚠️ Unknown device type: " + deviceName + " (class: " + deviceClass + ")");
            return new DeviceDetectionResult(device, DeviceType.GENERIC,
                    "Unknown type, defaulting to generic", true);

        } catch (Exception e) {
            Log.e(TAG, "❌ Error detecting device type: " + e.getMessage());
            return new DeviceDetectionResult(device, DeviceType.DISCONNECTED,
                    "Detection error: " + e.getMessage(), false);
        }
    }

    /**
     * UUID kontrolü - AirPods için en güvenilir yöntem
     */
    private static boolean hasAirPodsUUID(BluetoothDevice device) {
        try {
            ParcelUuid[] uuids = device.getUuids();
            if (uuids == null) return false;

            for (ParcelUuid deviceUuid : uuids) {
                for (ParcelUuid airpodsUuid : AIRPODS_UUIDS) {
                    if (deviceUuid.equals(airpodsUuid)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error checking UUIDs: " + e.getMessage());
        }
        return false;
    }

    /**
     * İsim kontrolü - AirPods/Beats detection
     */
    private static boolean isAirPodsName(String deviceName) {
        if (deviceName == null) return false;

        for (String airpodsName : AIRPODS_NAMES) {
            if (deviceName.contains(airpodsName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Device class kontrolü - kulaklık olup olmadığını kontrol eder
     */
    private static boolean isHeadphoneClass(int deviceClass) {
        return deviceClass == DEVICE_CLASS_HEADPHONES ||
                deviceClass == DEVICE_CLASS_HEADSET;
    }

    /**
     * Güvenli device name alma
     */
    private static String getDeviceName(BluetoothDevice device) {
        try {
            String name = device.getName();

            // API 30+ için alias kontrolü
            if ((name == null || name.isEmpty()) && android.os.Build.VERSION.SDK_INT >= 30) {
                name = device.getAlias();
            }

            // Son çare: MAC address
            if (name == null || name.isEmpty()) {
                name = device.getAddress();
            }

            return name != null ? name : "Unknown Device";

        } catch (Exception e) {
            Log.e(TAG, "Error getting device name: " + e.getMessage());
            return "Unknown Device";
        }
    }

    /**
     * Debug için device bilgilerini logla
     */
    public static void logDeviceInfo(BluetoothDevice device) {
        if (device == null) return;

        try {
            Log.d(TAG, "📱 Device Info:");
            Log.d(TAG, "  Name: " + getDeviceName(device));
            Log.d(TAG, "  Address: " + device.getAddress());
            Log.d(TAG, "  Class: " + device.getBluetoothClass().getDeviceClass());
            Log.d(TAG, "  Bond State: " + device.getBondState());

            ParcelUuid[] uuids = device.getUuids();
            if (uuids != null) {
                Log.d(TAG, "  UUIDs:");
                for (ParcelUuid uuid : uuids) {
                    Log.d(TAG, "    " + uuid.toString());
                }
            } else {
                Log.d(TAG, "  UUIDs: null");
            }
        } catch (Exception e) {
            Log.e(TAG, "Error logging device info: " + e.getMessage());
        }
    }

    /**
     * Device'ın bağlı olup olmadığını kontrol et (mevcut koddan)
     */
    public static boolean isDeviceConnected(BluetoothDevice device) {
        try {
            return (Boolean) device.getClass()
                    .getMethod("isConnected")
                    .invoke(device);
        } catch (Exception e) {
            Log.e(TAG, "Error checking connection status: " + e.getMessage());
            return false;
        }
    }
}