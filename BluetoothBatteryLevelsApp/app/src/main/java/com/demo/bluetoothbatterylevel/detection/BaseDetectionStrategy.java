package com.demo.bluetoothbatterylevel.detection;

import android.content.Context;
import android.util.Log;

import com.demo.bluetoothbatterylevel.model.BatteryData;

public abstract class BaseDetectionStrategy implements BatteryDetectionStrategy {
    protected static final String TAG = "BaseDetectionStrategy";

    protected Context context;
    protected BatteryDetectionListener listener;
    protected boolean isActive = false;
    protected String strategyName;

    public BaseDetectionStrategy(Context context, String strategyName) {
        this.context = context;
        this.strategyName = strategyName;
    }

    @Override
    public void setBatteryListener(BatteryDetectionListener listener) {
        this.listener = listener;
        Log.d(TAG, strategyName + " listener set");
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    public String getStrategyType() {
        return strategyName;
    }

    /**
     * Listener'a battery data gönder
     */
    protected void notifyBatteryData(BatteryData batteryData) {
        if (listener != null) {
            Log.d(TAG, strategyName + " sending battery data: " + batteryData.toString());
            listener.onBatteryDataReceived(batteryData);
        } else {
            Log.w(TAG, strategyName + " listener is null, cannot send battery data");
        }
    }

    /**
     * Listener'a connection event gönder
     */
    protected void notifyDeviceConnected(android.bluetooth.BluetoothDevice device) {
        if (listener != null) {
            Log.d(TAG, strategyName + " device connected: " +
                    (device != null ? device.getName() : "null"));
            listener.onDeviceConnected(device);
        }
    }

    /**
     * Listener'a disconnection event gönder
     */
    protected void notifyDeviceDisconnected(android.bluetooth.BluetoothDevice device) {
        if (listener != null) {
            Log.d(TAG, strategyName + " device disconnected: " +
                    (device != null ? device.getName() : "null"));
            listener.onDeviceDisconnected(device);
        }
    }

    /**
     * Listener'a error gönder
     */
    protected void notifyError(String error, Exception exception) {
        if (listener != null) {
            Log.e(TAG, strategyName + " error: " + error, exception);
            listener.onDetectionError(error, exception);
        } else {
            Log.e(TAG, strategyName + " error (no listener): " + error, exception);
        }
    }

    /**
     * Listener'a status change gönder
     */
    protected void notifyStatusChange(String status) {
        if (listener != null) {
            Log.d(TAG, strategyName + " status: " + status);
            listener.onDetectionStatusChanged(status);
        }
    }

    /**
     * Context null check
     */
    protected boolean isContextValid() {
        if (context == null) {
            Log.e(TAG, strategyName + " context is null");
            return false;
        }
        return true;
    }
}