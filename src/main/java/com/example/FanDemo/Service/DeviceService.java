package com.example.FanDemo.Service;

import com.example.FanDemo.Model.Device;
import com.example.FanDemo.Repository.DeviceRepository;
import org.springframework.stereotype.Service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

@Service
public class DeviceService {

    private final DeviceRepository deviceRepository;
    private final UdpDiscoveryService udpDiscoveryService;

    public DeviceService(DeviceRepository deviceRepository, UdpDiscoveryService udpDiscoveryService) {
        this.deviceRepository = deviceRepository;
        this.udpDiscoveryService = udpDiscoveryService;
    }

    public Collection<Device> getAvailableDevices() {
        return udpDiscoveryService.getOnlineDevices().values();
    }

    // --------------------- Connect / Disconnect ---------------------
    public Device connectDevice(String name, String userEmail) {
        Device device = udpDiscoveryService.getOnlineDevices().get(name);
        if (device != null) {
            device.setConnected(true);
            Device.UsageLog log = new Device.UsageLog();
            log.setUserEmail(userEmail);
            log.setUsageHours(0.0);
            if (device.getUsageLogs() == null) device.setUsageLogs(new ArrayList<>());
            device.getUsageLogs().add(log);
            deviceRepository.save(device);

            sendUdpCommand(device, "{\"cmd\":\"connect\"}");
        }
        return device;
    }

    public Device disconnectDevice(String name, String userEmail, double usageHours) {
        Device device = udpDiscoveryService.getOnlineDevices().get(name);
        if (device != null) {
            device.setConnected(false);
            device.setGlobalPower(false);
            if (device.getFans() != null) {
                for (Device.Fan fan : device.getFans()) {
                    fan.setPower(false);
                    fan.setSpeed(0);
                    fan.setBoost(false);
                }
            }
            Device.UsageLog log = new Device.UsageLog();
            log.setUserEmail(userEmail);
            log.setUsageHours(usageHours);
            if (device.getUsageLogs() == null) device.setUsageLogs(new ArrayList<>());
            device.getUsageLogs().add(log);
            deviceRepository.save(device);

            sendUdpCommand(device, "{\"cmd\":\"disconnect\"}");
        }
        return device;
    }

    // --------------------- Fan Controls ---------------------
    public Device toggleGlobalPower(String name, boolean power) {
        Device device = udpDiscoveryService.getOnlineDevices().get(name);
        if (device != null) {
            device.setGlobalPower(power);
            if (device.getFans() != null) {
                for (Device.Fan fan : device.getFans()) fan.setPower(power);
            }
            sendUdpCommand(device, generateFanControlJson(device));
        }
        return device;
    }

    public Device toggleFanPower(String name, int index, boolean power) {
        Device device = udpDiscoveryService.getOnlineDevices().get(name);
        if (device != null && index >= 0 && index < 2 && device.getFans() != null) {
            device.getFans()[index].setPower(power);
            sendUdpCommand(device, generateFanControlJson(device));
        }
        return device;
    }

    public Device setFanSpeed(String name, int index, int speed) {
        Device device = udpDiscoveryService.getOnlineDevices().get(name);
        if (device != null && index >= 0 && index < 2 && device.getFans() != null) {
            device.getFans()[index].setSpeed(speed);
            sendUdpCommand(device, generateFanControlJson(device));
        }
        return device;
    }

    public Device activateBoost(String name, int index, int duration) {
        Device device = udpDiscoveryService.getOnlineDevices().get(name);
        if (device != null && index >= 0 && index < 2 && device.getFans() != null) {
            device.getFans()[index].setBoost(true);
            device.getFans()[index].setBoostTime(duration);
            sendUdpCommand(device, generateFanControlJson(device));
        }
        return device;
    }

    // --------------------- Usage ---------------------
    public Map<String, Double> getUsageByUser(String userEmail) {
        List<Device> allDevices = deviceRepository.findAll();
        Map<String, Double> usageMap = new HashMap<>();
        for (Device device : allDevices) {
            if (device.getUsageLogs() != null) {
                double totalUsage = device.getUsageLogs().stream()
                        .filter(log -> log.getUserEmail().equals(userEmail))
                        .mapToDouble(Device.UsageLog::getUsageHours)
                        .sum();
                if (totalUsage > 0) usageMap.put(device.getName(), totalUsage);
            }
        }
        return usageMap;
    }

    // --------------------- UDP COMMAND ---------------------
    private void sendUdpCommand(Device device, String jsonCommand) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(device.getIp());
            byte[] data = jsonCommand.getBytes();
            DatagramPacket packet = new DatagramPacket(data, data.length, address, 4211); // ESP32 listening port
            socket.send(packet);
            System.out.println("UDP command sent to " + device.getName() + ": " + jsonCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateFanControlJson(Device device) {
        String fan1Json = String.format(
                "{\"power\":%b,\"speed\":%d,\"boost\":%b,\"boostTime\":%d}",
                device.getFans()[0].isPower(),
                device.getFans()[0].getSpeed(),
                device.getFans()[0].isBoost(),
                device.getFans()[0].getBoostTime()
        );

        String fan2Json = String.format(
                "{\"power\":%b,\"speed\":%d,\"boost\":%b,\"boostTime\":%d}",
                device.getFans()[1].isPower(),
                device.getFans()[1].getSpeed(),
                device.getFans()[1].isBoost(),
                device.getFans()[1].getBoostTime()
        );

        return String.format("{\"cmd\":\"fanControl\",\"fan1\":%s,\"fan2\":%s}", fan1Json, fan2Json);
    }
}
