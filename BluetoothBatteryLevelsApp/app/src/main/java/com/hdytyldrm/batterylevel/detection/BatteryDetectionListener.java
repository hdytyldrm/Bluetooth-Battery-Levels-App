package com.hdytyldrm.batterylevel.detection;

import android.bluetooth.BluetoothDevice;

import com.hdytyldrm.batterylevel.model.BatteryData;

public interface BatteryDetectionListener {

    /**
     * Yeni battery data algılandığında çağrılır
     */
    void onBatteryDataReceived(BatteryData batteryData);

    /**
     * Cihaz bağlandığında çağrılır
     */
    void onDeviceConnected(BluetoothDevice device);

    /**
     * Cihaz bağlantısı kesildiğinde çağrılır
     */
    void onDeviceDisconnected(BluetoothDevice device);

    /**
     * Detection error olduğunda çağrılır
     */
    void onDetectionError(String error, Exception exception);

    /**
     * Detection durumu değiştiğinde çağrılır
     */
    void onDetectionStatusChanged(String status);
}
