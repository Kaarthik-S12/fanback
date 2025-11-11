package com.example.FanDemo.Service;

import com.example.FanDemo.Model.Device;
import com.example.FanDemo.Repository.DeviceRepository;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UdpDiscoveryService {

    private final DeviceRepository deviceRepository;
    private final ConcurrentHashMap<String, Device> onlineDevices = new ConcurrentHashMap<>();
    private static final int UDP_PORT = 4210;

    public UdpDiscoveryService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    // Get currently online devices
    public ConcurrentHashMap<String, Device> getOnlineDevices() {
        return onlineDevices;
    }

    // Start listening for ESP32 broadcasts
    @PostConstruct
    public void startListener() {
        new Thread(() -> {
            try (DatagramSocket socket = new DatagramSocket(UDP_PORT)) {
                byte[] buffer = new byte[512];
                System.out.println("UDP Listener started on port " + UDP_PORT);

                while (true) {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet);

                    String msg = new String(packet.getData(), 0, packet.getLength());
                    JSONObject obj = new JSONObject(msg);

                    String name = obj.getString("name");
                    String ip = obj.getString("ip"); // ESP32 IP from broadcast

                    // Check if device exists in memory
                    Device device = onlineDevices.get(name);

                    if (device == null) {
                        // Check DB if device exists
                        device = deviceRepository.findByName(name);
                        if (device == null) {
                            // New device
                            device = new Device();
                            device.setName(name);
                            device.setFans(new Device.Fan[]{new Device.Fan(), new Device.Fan()});
                            device.setUsageLogs(new ArrayList<>());
                        }
                    }

                    // Update IP and mark as connected
                    device.setIp(ip);
                    device.setConnected(true);

                    // Add/update in memory
                    onlineDevices.put(name, device);

                    // Save/update in MongoDB
                    deviceRepository.save(device);

                    System.out.println("Device discovered: " + name + " | IP: " + ip);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
