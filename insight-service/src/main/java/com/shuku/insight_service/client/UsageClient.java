package com.shuku.insight_service.client;

import com.shuku.insight_service.dto.UsageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
@Slf4j
@Component
public class UsageClient{

    private final RestTemplate restTemplate;

    private final String baseurl;

    public UsageClient(@Value("${usage.service.url}") String baseurl, RestTemplate restTemplate){
        this.restTemplate= restTemplate;
        this.baseurl=baseurl;
    }

    public UsageDto getXDaysUsageForUser(Long userId,int days){
        String url = UriComponentsBuilder
                .fromUriString(baseurl)
                .path("/{userId}")
                .queryParam("days",days)
                .buildAndExpand(userId)
                .toUriString();
        ResponseEntity<UsageDto> response = restTemplate.getForEntity(url, UsageDto.class);
        return response.getBody();


    }
}
