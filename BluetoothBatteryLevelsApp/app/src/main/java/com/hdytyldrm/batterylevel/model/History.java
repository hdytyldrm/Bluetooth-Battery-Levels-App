package com.hdytyldrm.batterylevel.model;

public class History {
    public int battery;
    public String date;
    public int id;
    public String name;
    public String status;
    public String time;
    public int type;

    public History(String str, String str2, String str3, String str4, int i, int i2) {
        this.name = str;
        this.date = str2;
        this.time = str3;
        this.status = str4;
        this.battery = i;
        this.type = i2;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int i) {
        this.id = i;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getTime() {
        return this.time;
    }

    public void setTime(String str) {
        this.time = str;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String str) {
        this.status = str;
    }

    public int getBattery() {
        return this.battery;
    }

    public void setBattery(int i) {
        this.battery = i;
    }

    public String getDate() {
        return this.date;
    }

    public void setDate(String str) {
        this.date = str;
    }

    public int getType() {
        return this.type;
    }

    public void setType(int i) {
        this.type = i;
    }
}
