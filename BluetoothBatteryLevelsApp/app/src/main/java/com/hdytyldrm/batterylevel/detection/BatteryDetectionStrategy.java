package com.hdytyldrm.batterylevel.detection;

import android.bluetooth.BluetoothDevice;

public interface BatteryDetectionStrategy {

    /**
     * Detection'ı başlat
     */
    void startDetection();

    /**
     * Detection'ı durdur
     */
    void stopDetection();

    /**
     * Belirli bir cihaz için detection yap
     */
    void detectDevice(BluetoothDevice device);

    /**
     * Strategy'nin aktif olup olmadığını kontrol et
     */
    boolean isActive();

    /**
     * Strategy tipi (debug için)
     */
    String getStrategyType();

    /**
     * Listener set et (callback için)
     */
    void setBatteryListener(BatteryDetectionListener listener);


}
