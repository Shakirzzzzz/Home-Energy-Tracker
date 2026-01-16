package com.shuku.device_service.controller;

import com.shuku.device_service.dto.DeviceDto;
import com.shuku.device_service.entity.Device;
import com.shuku.device_service.service.DeviceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/device")
@Tag(name = "Device Actions Endpoints", description = "Operations related to the home devices")
public class DeviceController {

    private DeviceService deviceService;

    public DeviceController(DeviceService deviceService){
        this.deviceService = deviceService;
    }


    @GetMapping("/{id}")
    @Operation(summary = "Gets device details", description = "Fetch complete description about a device using its device id")
    public ResponseEntity<DeviceDto> getDeviceById(@PathVariable Long id){
        DeviceDto deviceDto = deviceService.getDeviceById(id);
        return ResponseEntity.ok(deviceDto);
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Gets all the devices belonging to a user", description = "Fetch all the devices belonging to a user using his/her user id")
    public ResponseEntity<List<DeviceDto>> getAllDevicesByUserId(@PathVariable Long userId){
        List<DeviceDto> devices = deviceService.getAllDevicesByUserId(userId);
        return ResponseEntity.ok(devices);
    }

    @PostMapping("/create")
    @Operation(summary = "Creates a device in the database", description = "Create a device and store it in the database")
    public ResponseEntity<DeviceDto> createDevice(@RequestBody DeviceDto deviceDto){
        DeviceDto createdDevice = deviceService.createDevice(deviceDto);
        return ResponseEntity.ok(createdDevice);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Updates device details ", description = "Updates the properties of a device")
    public ResponseEntity<DeviceDto> updateDevice(@PathVariable Long id, @RequestBody DeviceDto deviceDto){
        DeviceDto updatedDevice = deviceService.updateDevice(id,deviceDto);
        return ResponseEntity.ok(updatedDevice);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Deletes a device", description = "Removes a device belonging to a user from the database")
    public ResponseEntity<String> deleteDevice(@PathVariable Long id){
        deviceService.deleteDevice(id);
        return new ResponseEntity<>("Device Removed", HttpStatus.OK);
    }
}
