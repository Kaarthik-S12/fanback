package com.example.FanDemo.Repository;

import com.example.FanDemo.Model.Device;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends MongoRepository<Device, String> {

    // Find a device by its unique name
    Device findByName(String name);

    // Check if a device exists by name
    boolean existsByName(String name);

    // Get all devices currently connected
    List<Device> findByConnectedTrue();

    // Get devices used by a specific user
    List<Device> findByUsageLogsUserEmail(String userEmail);
}
