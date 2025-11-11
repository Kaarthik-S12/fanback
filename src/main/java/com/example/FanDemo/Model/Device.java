package com.example.FanDemo.Model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "devices")
public class Device {

    @Id
    private String id;
    private String name;
    private boolean connected;
    private boolean globalPower;
    private String ip; // store ESP32 IP
    private Fan[] fans;
    private List<UsageLog> usageLogs = new ArrayList<>();

    // --------------------- Fan Inner Class ---------------------
    public static class Fan {
        private boolean power;
        private int speed;
        private boolean boost;
        private int boostTime;

        public boolean isPower() { return power; }
        public void setPower(boolean power) { this.power = power; }

        public int getSpeed() { return speed; }
        public void setSpeed(int speed) { this.speed = speed; }

        public boolean isBoost() { return boost; }
        public void setBoost(boolean boost) { this.boost = boost; }

        public int getBoostTime() { return boostTime; }
        public void setBoostTime(int boostTime) { this.boostTime = boostTime; }
    }

    // --------------------- UsageLog Inner Class ---------------------
    public static class UsageLog {
        private String userEmail;
        private double usageHours;

        public String getUserEmail() { return userEmail; }
        public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

        public double getUsageHours() { return usageHours; }
        public void setUsageHours(double usageHours) { this.usageHours = usageHours; }
    }

    // --------------------- Device Getters/Setters ---------------------
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean isConnected() { return connected; }
    public void setConnected(boolean connected) { this.connected = connected; }

    public boolean isGlobalPower() { return globalPower; }
    public void setGlobalPower(boolean globalPower) { this.globalPower = globalPower; }

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public Fan[] getFans() { return fans; }
    public void setFans(Fan[] fans) { this.fans = fans; }

    public List<UsageLog> getUsageLogs() { return usageLogs; }
    public void setUsageLogs(List<UsageLog> usageLogs) { this.usageLogs = usageLogs; }
}
