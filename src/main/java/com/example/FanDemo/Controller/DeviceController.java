package com.example.FanDemo.Controller;

import com.example.FanDemo.Model.Device;
import com.example.FanDemo.Service.DeviceService;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@CrossOrigin(origins = "http://localhost:5173")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping("/available")
    public Collection<Device> getAvailableDevices() {
        return deviceService.getAvailableDevices();
    }

    @PostMapping("/connect/{name}")
    public Device connectDevice(@PathVariable String name, @RequestParam String email) {
        return deviceService.connectDevice(name, email);
    }

    @PostMapping("/disconnect/{name}")
    public Device disconnectDevice(@PathVariable String name, @RequestParam String email, @RequestParam double usageHours) {
        return deviceService.disconnectDevice(name, email, usageHours);
    }

    @GetMapping("/usage/{email}")
    public Map<String, Double> getUsageByUser(@PathVariable String email) {
        return deviceService.getUsageByUser(email);
    }

    @PostMapping("/{name}/globalPower")
    public Device toggleGlobalPower(@PathVariable String name, @RequestParam boolean power) {
        return deviceService.toggleGlobalPower(name, power);
    }

    @PostMapping("/{name}/fan/{index}/power")
    public Device toggleFanPower(@PathVariable String name, @PathVariable int index, @RequestParam boolean power) {
        return deviceService.toggleFanPower(name, index, power);
    }

    @PostMapping("/{name}/fan/{index}/speed")
    public Device setFanSpeed(@PathVariable String name, @PathVariable int index, @RequestParam int speed) {
        return deviceService.setFanSpeed(name, index, speed);
    }

    @PostMapping("/{name}/fan/{index}/boost")
    public Device activateBoost(@PathVariable String name, @PathVariable int index, @RequestParam int duration) {
        return deviceService.activateBoost(name, index, duration);
    }
}
