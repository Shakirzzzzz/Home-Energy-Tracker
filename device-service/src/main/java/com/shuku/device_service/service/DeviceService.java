package com.shuku.device_service.service;

import com.shuku.device_service.dto.DeviceDto;
import com.shuku.device_service.entity.Device;
import com.shuku.device_service.repository.DeviceRepository;
import org.springframework.stereotype.Service;

@Service
public class DeviceService {

    private DeviceRepository deviceRepository;


    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;

    }

    public DeviceDto getDeviceById(Long Id) {
        Device device = deviceRepository.findById(Id).orElseThrow(() -> new IllegalArgumentException("Device not found with id" + Id));
        return mapToDto(device);

    }

    public DeviceDto createDevice(DeviceDto input){
        Device device = new Device();
        device.setId(input.getId());
        device.setName(input.getName());
        device.setType(input.getType());
        device.setLocation(input.getLocation());
        device.setUserId(input.getUserId());

        Device savedDevice = deviceRepository.save(device);
        return mapToDto(savedDevice);
    }

    public DeviceDto updateDevice(Long id, DeviceDto input){
        Device existingDevice = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Device not found with id: " + id));
        existingDevice.setName(input.getName());
        existingDevice.setType(input.getType());
        existingDevice.setLocation(input.getLocation());
        existingDevice.setUserId(input.getUserId());
        final Device updatedDevice = deviceRepository.save(existingDevice);
        return mapToDto(updatedDevice);
    }

    public void deleteDevice(Long id){
        Device toRemove = deviceRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("User not found with the id: " + id));
        deviceRepository.delete(toRemove);
    }

    private DeviceDto mapToDto(Device device) {
        DeviceDto deviceDto = new DeviceDto();
        deviceDto.setId(device.getId());
        deviceDto.setName(device.getName());
        deviceDto.setType(device.getType());
        deviceDto.setLocation(device.getLocation());
        deviceDto.setUserId(device.getUserId());
        return deviceDto;
    }

}
