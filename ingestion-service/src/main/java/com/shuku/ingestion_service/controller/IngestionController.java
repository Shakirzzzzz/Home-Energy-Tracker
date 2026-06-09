package com.shuku.ingestion_service.controller;

import com.shuku.ingestion_service.dto.EnergyUsageDto;
import com.shuku.ingestion_service.service.IngestionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/ingestion")
@Tag(name= "Ingestion Endpoint", description = "Endpoint to ingest data of an IOT Device")
public class IngestionController {

    private  final IngestionService ingestionService;

    public IngestionController(IngestionService ingestionService){
        this.ingestionService = ingestionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Takes data from an IOT device", description = "This endpoint can be used to ingest data of an IOT device for a user")
    public void ingestData(@RequestBody EnergyUsageDto usageDto){
        ingestionService.ingestEnergyUsage(usageDto);
    }
}
