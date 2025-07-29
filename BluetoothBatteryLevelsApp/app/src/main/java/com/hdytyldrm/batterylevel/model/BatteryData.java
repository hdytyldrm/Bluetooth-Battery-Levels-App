package com.hdytyldrm.batterylevel.model;

import android.os.Parcel;
import android.os.Parcelable;

public class BatteryData implements Parcelable {
    private DeviceType deviceType;
    private String deviceName;
    private String deviceAddress;

    // Apple audio device specific data
    private String leftBattery = "--";
    private String rightBattery = "--";
    private String caseBattery = "--";
    private boolean leftCharging = false;
    private boolean rightCharging = false;
    private boolean caseCharging = false;
    private boolean leftInEar = false;
    private boolean rightInEar = false;

    private boolean isConnected = false;
    private long timestamp = System.currentTimeMillis();

    // Constructors
    public BatteryData() {
        this.deviceType = DeviceType.DISCONNECTED;
    }

    // Apple audio device constructor (Primary constructor)
    public BatteryData(String deviceName, String deviceAddress, String left, String right, String caseB,
                       boolean leftChg, boolean rightChg, boolean caseChg, boolean leftInEar, boolean rightInEar) {
        this.deviceType = DeviceType.AIRPODS; // All Apple devices use this type
        this.deviceName = enhanceAppleDeviceName(deviceName);
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

    // Getters and Setters
    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = enhanceAppleDeviceName(deviceName); }

    public String getDeviceAddress() { return deviceAddress; }
    public void setDeviceAddress(String deviceAddress) { this.deviceAddress = deviceAddress; }

    public String getLeftBattery() { return leftBattery; }
    public void setLeftBattery(String leftBattery) { this.leftBattery = leftBattery; }

    public String getRightBattery() { return rightBattery; }
    public void setRightBattery(String rightBattery) { this.rightBattery = rightBattery; }

    public String getCaseBattery() { return caseBattery; }
    public void setCaseBattery(String caseBattery) { this.caseBattery = caseBattery; }

    public boolean isLeftCharging() { return leftCharging; }
    public void setLeftCharging(boolean leftCharging) { this.leftCharging = leftCharging; }

    public boolean isRightCharging() { return rightCharging; }
    public void setRightCharging(boolean rightCharging) { this.rightCharging = rightCharging; }

    public boolean isCaseCharging() { return caseCharging; }
    public void setCaseCharging(boolean caseCharging) { this.caseCharging = caseCharging; }

    public boolean isLeftInEar() { return leftInEar; }
    public void setLeftInEar(boolean leftInEar) { this.leftInEar = leftInEar; }

    public boolean isRightInEar() { return rightInEar; }
    public void setRightInEar(boolean rightInEar) { this.rightInEar = rightInEar; }

    public boolean isConnected() { return isConnected; }
    public void setConnected(boolean connected) { isConnected = connected; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    // Utility methods - Simplified for Apple devices only
    public boolean isAirPods() {
        return deviceType == DeviceType.AIRPODS; // All Apple audio devices
    }

    @Deprecated
    public boolean isGeneric() {
        return false; // No longer supported
    }

    public boolean isDisconnected() {
        return deviceType == DeviceType.DISCONNECTED || !isConnected;
    }

    /**
     * Enhanced Apple device name detection
     */
    private String enhanceAppleDeviceName(String deviceName) {
        if (deviceName == null || deviceName.isEmpty()) {
            return "Apple Audio Device";
        }

        String lowerName = deviceName.toLowerCase();

        // AirPods family
        if (lowerName.contains("airpods")) {
            if (lowerName.contains("pro") && lowerName.contains("2")) return "AirPods Pro (2nd gen)";
            if (lowerName.contains("pro")) return "AirPods Pro";
            if (lowerName.contains("max")) return "AirPods Max";
            if (lowerName.contains("3rd") || lowerName.contains("(3")) return "AirPods (3rd gen)";
            if (lowerName.contains("2nd") || lowerName.contains("(2")) return "AirPods (2nd gen)";
            return "AirPods";
        }

        // Beats family
        if (lowerName.contains("beats")) {
            if (lowerName.contains("solo 3") || lowerName.contains("solo3")) return "Beats Solo 3";
            if (lowerName.contains("studio 3") || lowerName.contains("studio3")) return "Beats Studio 3";
            if (lowerName.contains("powerbeats pro")) return "Powerbeats Pro";
            if (lowerName.contains("powerbeats 3")) return "Powerbeats 3";
            if (lowerName.contains("flex")) return "Beats Flex";
            if (lowerName.contains("solo")) return "Beats Solo";
            if (lowerName.contains("studio")) return "Beats Studio";
            if (lowerName.contains("beatsx") || lowerName.contains("beats x")) return "Beats X";
            return "Beats";
        }

        return deviceName;
    }

    /**
     * Get device model type for enhanced UI
     */
    public String getDeviceModel() {
        if (deviceName == null) return "unknown";

        String lowerName = deviceName.toLowerCase();

        if (lowerName.contains("airpods pro")) return "airpods_pro";
        if (lowerName.contains("airpods max")) return "airpods_max";
        if (lowerName.contains("airpods")) return "airpods";
        if (lowerName.contains("beats")) return "beats";

        return "apple_audio";
    }

    /**
     * Check if device supports case battery
     */
    public boolean supportsCaseBattery() {
        String model = getDeviceModel();
        // AirPods Max doesn't have a case, most others do
        return !model.equals("airpods_max");
    }

    @Override
    public String toString() {
        if (isAirPods()) {
            return String.format("AppleAudio[%s] L:%s R:%s Case:%s Connected:%b",
                    deviceName, leftBattery, rightBattery, caseBattery, isConnected);
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
        dest.writeByte((byte) (isConnected ? 1 : 0));
        dest.writeLong(timestamp);
    }
}