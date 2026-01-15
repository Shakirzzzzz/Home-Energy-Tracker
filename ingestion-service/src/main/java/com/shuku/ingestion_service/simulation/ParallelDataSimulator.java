package com.shuku.ingestion_service.simulation;


import com.shuku.ingestion_service.dto.EnergyUsageDto;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Component
public class ParallelDataSimulator implements CommandLineRunner {

    private final RestTemplate restTemplate = new RestTemplate();
    private final ExecutorService executorService;


    private final int parallelThreads;

    @Value("${simulation.requests-per-interval}")
    private int requestsPerInterval;

    @Value("${simulation.endpoint}")
    private String ingestionEndpoint;

    public ParallelDataSimulator(@Value("${simulation.parallel-threads}") int parallelThreads) {
        this.parallelThreads = parallelThreads;
        this.executorService = Executors.newFixedThreadPool(parallelThreads);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("ParallelDataSimulator started...");
    }

    @Scheduled(fixedRateString = "${simulation.interval-ms}")
    public void sendMockData() {
        int batchSize = requestsPerInterval / parallelThreads;
        int remainder = requestsPerInterval % parallelThreads;

        for (int i = 0; i < parallelThreads; i++) {
            int requestsForThread = batchSize + (i < remainder ? 1 : 0);
            executorService.submit(
                    () -> {
                        for (int j = 0; j < requestsForThread; j++) {
                            EnergyUsageDto dto = EnergyUsageDto.builder()
                                    .deviceId(ThreadLocalRandom.current().nextLong(1, 20))
                                    .energyConsumed(Math.round(ThreadLocalRandom.current().nextDouble(0.0, 2.0) * 100) / 100.0)
                                    .timestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant())
                                    .build();
                            try {
                                HttpHeaders httpHeaders = new HttpHeaders();
                                httpHeaders.setContentType(MediaType.APPLICATION_JSON);

                                HttpEntity<EnergyUsageDto> request = new HttpEntity<>(dto, httpHeaders);
                                restTemplate.postForEntity(ingestionEndpoint, request, Void.class);
                                log.info("Sent mock data: {}", dto);
                            } catch (Exception exe) {
                                log.error("Failed to send data: {}", exe.getMessage());

                            }
                        }

                    });

        }
    }

    @PreDestroy
    public void shutdown() {
        executorService.shutdown();
        log.info("ParallelDataSimulator shut down...");
    }
}
