package com.hdytyldrm.batterylevel.model;

import android.bluetooth.BluetoothDevice;

public class DeviceDetectionResult {
    private BluetoothDevice device;
    private DeviceType detectedType;
    private String reason;
    private boolean isValid;

    public DeviceDetectionResult(BluetoothDevice device, DeviceType detectedType, String reason, boolean isValid) {
        this.device = device;
        this.detectedType = detectedType;
        this.reason = reason;
        this.isValid = isValid;
    }

    // Getters
    public BluetoothDevice getDevice() { return device; }
    public DeviceType getDetectedType() { return detectedType; }
    public String getReason() { return reason; }
    public boolean isValid() { return isValid; }

    @Override
    public String toString() {
        return String.format("Detection[%s] Type:%s Valid:%b Reason:%s",
                device != null ? device.getName() : "null",
                detectedType, isValid, reason);
    }
}