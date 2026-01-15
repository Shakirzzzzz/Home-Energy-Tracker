package com.shuku.usage_service.client;

import com.shuku.usage_service.Dto.DeviceDto;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.HttpExchange;

import java.util.List;

@HttpExchange(url = "http://localhost:8081/api/v1/device")
public interface DeviceClient {

    @GetExchange("/{id}")
    DeviceDto getDeviceById(@PathVariable Long id);

    @GetExchange("/user/{userId}")
    List<DeviceDto> getAllDevicesForUser(@PathVariable Long userId);
}
