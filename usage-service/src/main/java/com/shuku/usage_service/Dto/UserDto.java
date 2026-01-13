package com.shuku.usage_service.Dto;

import lombok.Builder;

@Builder
public record UserDto(
        Long id,
        String name,
        String surname,
        String email,
        String address,
        boolean alerting,
        double energyAlertingThreshold
) {
}
