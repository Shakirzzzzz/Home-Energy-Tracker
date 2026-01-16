package com.shuku.insight_service.controller;


import com.shuku.insight_service.dto.InsightDto;
import com.shuku.insight_service.service.InsightService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/insight")
@Tag(name = "Energy Insights(AI) Endpoints",description = "Operations that give insights on the energy usage by the devices using AI")
public class InsightController {

    private final InsightService insightService;

    public InsightController(InsightService insightService){
        this.insightService = insightService;
    }

    @GetMapping("/saving-tips/{userId}")
    @Operation(summary = "Insight based on the overall usage", description = "Gives insight on the total energy used by all the devices of a user.")
    public ResponseEntity<InsightDto> getSavingTips(@PathVariable Long userId){
        final InsightDto insightDto = insightService.getSavingsTips(userId);
        return ResponseEntity.ok(insightDto);
    }

    @GetMapping("/overview/{userId}")
    @Operation(summary = "Insight based on per device energy usage", description = "Gives insight based on per device energy usage")
    public ResponseEntity<InsightDto> getOverview(@PathVariable Long userId){
        final InsightDto insightDto = insightService.getOverview(userId);
        return ResponseEntity.ok(insightDto);
    }
}
