package com.hdytyldrm.batterylevel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BatteryData implements Parcelable {
    private DeviceType deviceType;
    private String deviceName;
    private String deviceAddress;

    // AirPods specific
    private String leftBattery = "--";
    private String rightBattery = "--";
    private String caseBattery = "--";
    private boolean leftCharging = false;
    private boolean rightCharging = false;
    private boolean caseCharging = false;
    private boolean leftInEar = false;
    private boolean rightInEar = false;

    // Generic device specific
    private String singleBattery = "--";
    private boolean singleCharging = false;

    private boolean isConnected = false;
    private long timestamp = System.currentTimeMillis();

    // Constructors
    public BatteryData() {
        this.deviceType = DeviceType.DISCONNECTED;
    }

    // AirPods constructor
    public BatteryData(String deviceName, String deviceAddress, String left, String right, String caseB,
                       boolean leftChg, boolean rightChg, boolean caseChg, boolean leftInEar, boolean rightInEar) {
        this.deviceType = DeviceType.AIRPODS;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.leftBattery = left;
        this.rightBattery = right;
        this.caseBattery = caseB;
        this.leftCharging = leftChg;
        this.rightCharging = rightChg;
        this.caseCharging = caseChg;
        this.leftInEar = leftInEar;
        this.rightInEar = rightInEar;
        this.isConnected = true;
        this.timestamp = System.currentTimeMillis();
    }

    // Generic device constructor
    public BatteryData(String deviceName, String deviceAddress, String battery, boolean charging) {
        this.deviceType = DeviceType.GENERIC;
        this.deviceName = deviceName;
        this.deviceAddress = deviceAddress;
        this.singleBattery = battery;
        this.singleCharging = charging;
        this.isConnected = true;
        this.timestamp = System.currentTimeMillis();
    }

    // Getters and Setters
    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public String getDeviceAddress() { return deviceAddress; }
    public void setDeviceAddress(String deviceAddress) { this.deviceAddress = deviceAddress; }

    public String getLeftBattery() { return leftBattery; }
    public void setLeftBattery(String leftBattery) { this.leftBattery = leftBattery; }

    public String getRightBattery() { return rightBattery; }
    public void setRightBattery(String rightBattery) { this.rightBattery = rightBattery; }

    public String getCaseBattery() { return caseBattery; }
    public void setCaseBattery(String caseBattery) { this.caseBattery = caseBattery; }

    public String getSingleBattery() { return singleBattery; }
    public void setSingleBattery(String singleBattery) { this.singleBattery = singleBattery; }

    public boolean isLeftCharging() { return leftCharging; }
    public void setLeftCharging(boolean leftCharging) { this.leftCharging = leftCharging; }

    public boolean isRightCharging() { return rightCharging; }
    public void setRightCharging(boolean rightCharging) { this.rightCharging = rightCharging; }

    public boolean isCaseCharging() { return caseCharging; }
    public void setCaseCharging(boolean caseCharging) { this.caseCharging = caseCharging; }

    public boolean isSingleCharging() { return singleCharging; }
    public void setSingleCharging(boolean singleCharging) { this.singleCharging = singleCharging; }

    public boolean isLeftInEar() { return leftInEar; }
    public void setLeftInEar(boolean leftInEar) { this.leftInEar = leftInEar; }

    public boolean isRightInEar() { return rightInEar; }
    public void setRightInEar(boolean rightInEar) { this.rightInEar = rightInEar; }

    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { isConnected = connected; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Utility methods
    public boolean isAirPods() {
        return deviceType == DeviceType.AIRPODS;
    }

    public boolean isGeneric() {
        return deviceType == DeviceType.GENERIC;
    }

    public boolean isDisconnected() {
        return deviceType == DeviceType.DISCONNECTED || !isConnected;
    }

    @Override
    public String toString() {
        if (isAirPods()) {
            return String.format("AirPods[%s] L:%s R:%s Case:%s Connected:%b",
                    deviceName, leftBattery, rightBattery, caseBattery, isConnected);
        } else if (isGeneric()) {
            return String.format("Generic[%s] Battery:%s Connected:%b",
                    deviceName, singleBattery, isConnected);
        } else {
            return "Disconnected";
        }
    }

    // ===== PARCELABLE IMPLEMENTATION =====

    protected BatteryData(Parcel in) {
        String deviceTypeString = in.readString();
        deviceType = deviceTypeString != null ? DeviceType.valueOf(deviceTypeString) : DeviceType.DISCONNECTED;
        deviceName = in.readString();
        deviceAddress = in.readString();
        leftBattery = in.readString();
        rightBattery = in.readString();
        caseBattery = in.readString();
        leftCharging = in.readByte() != 0;
        rightCharging = in.readByte() != 0;
        caseCharging = in.readByte() != 0;
        leftInEar = in.readByte() != 0;
        rightInEar = in.readByte() != 0;
        singleBattery = in.readString();
        singleCharging = in.readByte() != 0;
        isConnected = in.readByte() != 0;
        timestamp = in.readLong();
    }

    public static final Creator<BatteryData> CREATOR = new Creator<BatteryData>() {
        @Override
        public BatteryData createFromParcel(Parcel in) {
            return new BatteryData(in);
        }

        @Override
        public BatteryData[] newArray(int size) {
            return new BatteryData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(deviceType != null ? deviceType.name() : DeviceType.DISCONNECTED.name());
        dest.writeString(deviceName);
        dest.writeString(deviceAddress);
        dest.writeString(leftBattery);
        dest.writeString(rightBattery);
        dest.writeString(caseBattery);
        dest.writeByte((byte) (leftCharging ? 1 : 0));
        dest.writeByte((byte) (rightCharging ? 1 : 0));
        dest.writeByte((byte) (caseCharging ? 1 : 0));
        dest.writeByte((byte) (leftInEar ? 1 : 0));
        dest.writeByte((byte) (rightInEar ? 1 : 0));
        dest.writeString(singleBattery);
        dest.writeByte((byte) (singleCharging ? 1 : 0));
        dest.writeByte((byte) (isConnected ? 1 : 0));
        dest.writeLong(timestamp);
    }
}