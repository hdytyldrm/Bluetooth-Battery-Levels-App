package com.demo.bluetoothbatterylevel.detection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import com.demo.bluetoothbatterylevel.model.BatteryData;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

public class GenericBluetoothDetector extends BaseDetectionStrategy {
    private static final String TAG = "GenericBTDetector";

    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice currentDevice;

    public GenericBluetoothDetector(Context context) {
        super(context, "GenericBluetoothDetector");
        this.bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    // Olay dinleyicisi, artık servis yerine burada olacak.
    // GenericBluetoothDetector.java -> genericReceiver değişkenini bununla değiştirin.

    /*private final BroadcastReceiver genericReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null) return;

            // YENİ MANTIK: Gelen olay AirPods için mi kontrol et.
            boolean isAirPods = (device.getName() != null && device.getName().toLowerCase().contains("airpods"));

            if (isAirPods) {
                // Eğer olay AirPods içinse, bu dedektör işlem yapmaz,
                // ama bağlantı kesilme olayını servise bildirmesi gerekir ki servis durumu sıfırlasın.
                if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    Log.d(TAG, "AirPods disconnect event caught by GenericDetector, notifying service...");
                    notifyDeviceDisconnected(device);
                }
                return; // Başka bir işlem yapma.
            }

            // --- Buradan sonrası standart kulaklıklar için olan mevcut mantık ---
            Log.d(TAG, "GenericReceiver event: " + action + " for device " + device.getName());

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    currentDevice = device;
                    notifyDeviceConnected(device);
                    getBatteryLevelForDevice(device);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    if (currentDevice != null && currentDevice.getAddress().equals(device.getAddress())) {
                        currentDevice = null;
                        notifyDeviceDisconnected(device);
                        notifyBatteryData(new BatteryData());
                    }
                    break;
                case "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED":
                    if (currentDevice != null && currentDevice.getAddress().equals(device.getAddress())) {
                        int batteryLevel = intent.getIntExtra("android.bluetooth.device.extra.BATTERY_LEVEL", -1);
                        if (batteryLevel != -1) {
                            Log.d(TAG, "Battery level received from broadcast: " + batteryLevel + "%");
                            updateBatteryLevel(device, batteryLevel + "%");
                        }
                    }
                    break;
            }
        }
    };*/
// GenericBluetoothDetector.java -> genericReceiver değişkenini bununla değiştirin.

    private final BroadcastReceiver genericReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) return;

            BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
            if (device == null) return;

            // YENİ MANTIK: Gelen olay AirPods için mi kontrol et.
            boolean isAirPods = (device.getName() != null && device.getName().toLowerCase().contains("airpods"));

            if (isAirPods) {
                // Eğer olay AirPods içinse, bu dedektör işlem yapmaz,
                // ama bağlantı kesilme olayını servise bildirmesi gerekir ki servis durumu sıfırlasın.
                if (action.equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)) {
                    Log.d(TAG, "AirPods disconnect event caught by GenericDetector, notifying service...");
                    notifyDeviceDisconnected(device);
                }
                return; // Başka bir işlem yapma.
            }

            // --- Buradan sonrası standart kulaklıklar için olan mevcut mantık ---
            Log.d(TAG, "GenericReceiver event: " + action + " for device " + device.getName());

            switch (action) {
                case BluetoothDevice.ACTION_ACL_CONNECTED:
                    currentDevice = device;
                    notifyDeviceConnected(device);
                    getBatteryLevelForDevice(device);
                    break;
                case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                    if (currentDevice != null && currentDevice.getAddress().equals(device.getAddress())) {
                        currentDevice = null;
                        notifyDeviceDisconnected(device);
                        notifyBatteryData(new BatteryData());
                    }
                    break;
                case "android.bluetooth.device.action.BATTERY_LEVEL_CHANGED":
                    if (currentDevice != null && currentDevice.getAddress().equals(device.getAddress())) {
                        int batteryLevel = intent.getIntExtra("android.bluetooth.device.extra.BATTERY_LEVEL", -1);
                        if (batteryLevel != -1) {
                            Log.d(TAG, "Battery level received from broadcast: " + batteryLevel + "%");
                            updateBatteryLevel(device, batteryLevel + "%");
                        }
                    }
                    break;
            }
        }
    };
    @Override
    public void startDetection() {
        if (!isContextValid() || bluetoothAdapter == null) return;
        isActive = true;

        // Receiver'ı kaydet.
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction("android.bluetooth.device.action.BATTERY_LEVEL_CHANGED");
        context.registerReceiver(genericReceiver, filter);

        Log.d(TAG, "Generic Detector started and receiver registered.");
        checkAlreadyConnectedDevices();
        notifyStatusChange("Generic detection started");
    }

    @Override
    public void stopDetection() {
        if (!isContextValid() || !isActive) return;
        isActive = false;
        try {
            context.unregisterReceiver(genericReceiver);
            Log.d(TAG, "Generic Detector stopped and receiver unregistered.");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Receiver not registered.", e);
        }
        currentDevice = null;
        notifyStatusChange("Generic detection stopped");
    }

    @Override
    public void detectDevice(BluetoothDevice device) {
        // Bu metot artık doğrudan çağrılmayacak, tüm mantık receiver içinde.
        // Ama bir yere çağrılırsa diye pil seviyesini alabilir.
        getBatteryLevelForDevice(device);
    }

    private void checkAlreadyConnectedDevices() {
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) return;
        // A2DP (müzik) ve HEADSET (görüşme) profillerini kontrol et.
        bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.A2DP);
        bluetoothAdapter.getProfileProxy(context, serviceListener, BluetoothProfile.HEADSET);
    }

    private final BluetoothProfile.ServiceListener serviceListener = new BluetoothProfile.ServiceListener() {
        @Override
        public void onServiceConnected(int profile, BluetoothProfile proxy) {
            List<BluetoothDevice> connectedDevices = proxy.getConnectedDevices();
            if (connectedDevices != null && !connectedDevices.isEmpty()) {
                for(BluetoothDevice device : connectedDevices) {
                    if (device.getName() != null && device.getName().toLowerCase().contains("airpods")) {
                        continue; // AirPods'u atla
                    }
                    // Standart bir cihaz bulduk.
                    Log.d(TAG, "Found already connected generic device: " + device.getName());
                    currentDevice = device;
                    notifyDeviceConnected(device);
                    getBatteryLevelForDevice(device);
                    break; // Sadece ilk bulduğunu al.
                }
            }
            bluetoothAdapter.closeProfileProxy(profile, proxy);
        }

        @Override
        public void onServiceDisconnected(int profile) {}
    };

    private void getBatteryLevelForDevice(BluetoothDevice device) {
        if (device == null) return;

        try {
            Method method = device.getClass().getMethod("getBatteryLevel");
            Integer batteryLevel = (Integer) method.invoke(device);
            if (batteryLevel != null && batteryLevel != -1) {
                updateBatteryLevel(device, batteryLevel + "%");
                return;
            }
        } catch (Exception e) {
            Log.w(TAG, "Standard getBatteryLevel() failed, waiting for broadcast. " + e.getMessage());
        }
        // Eğer reflection ile alınamazsa, "Connected" diye bilgi gönder.
        // Zaten receiver'ımız BATTERY_LEVEL_CHANGED olayını yakalayıp güncelleyecek.
        updateBatteryLevel(device, "Connected");
    }

    private void updateBatteryLevel(BluetoothDevice device, String batteryLevel) {
        String deviceName = getDeviceName(device);
        Log.d(TAG, "✅ Battery update: " + deviceName + " = " + batteryLevel);
        notifyDeviceConnected(device);

        BatteryData batteryData = new BatteryData(deviceName, device.getAddress(), batteryLevel, false);
        notifyBatteryData(batteryData);
    }

    private String getDeviceName(BluetoothDevice device) {
        try {
            String name = device.getName();
            return (name != null && !name.isEmpty()) ? name : "Unknown Device";
        } catch (Exception e) {
            return "Unknown Device";
        }
    }
}