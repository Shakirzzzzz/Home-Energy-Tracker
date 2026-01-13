package com.shuku.usage_service.config;

import com.shuku.usage_service.client.DeviceClient;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration
@ImportHttpServices(basePackages = "com.shuku.usage_service.client",types = DeviceClient.class)
public class DeviceClientConfig {
}
