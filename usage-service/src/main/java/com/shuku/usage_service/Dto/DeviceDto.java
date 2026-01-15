package com.shuku.usage_service.Dto;

import lombok.Builder;

@Builder
public record DeviceDto(
        Long id,
        String name,
        String type,
        String location,
        Long userId,
        Double energyConsumed


) {
}
