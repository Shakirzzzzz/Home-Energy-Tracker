package com.shuku.usage_service.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class DeviceEnergy{
    private Long deviceId;
    private double energyConsumed;
    private Long userId;
}
