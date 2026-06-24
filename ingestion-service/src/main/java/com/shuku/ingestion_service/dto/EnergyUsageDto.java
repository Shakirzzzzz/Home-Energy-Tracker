package com.shuku.ingestion_service.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.time.Instant;
// using record here cuz we are only using this dto as data carrier there is no business logic and its immutable
@Builder
public record EnergyUsageDto(
    Long deviceId,
    double energyConsumed,
    //not necessary here its already serialized and deserialized as string
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    Instant timestamp) {}



