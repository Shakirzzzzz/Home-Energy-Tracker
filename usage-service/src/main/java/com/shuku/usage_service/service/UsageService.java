package com.shuku.usage_service.service;


import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;
import com.shuku.kafka.event.AlertingEvent;
import com.shuku.kafka.event.EnergyUsageEvent;
import com.shuku.usage_service.Dto.DeviceDto;
import com.shuku.usage_service.Dto.UserDto;
import com.shuku.usage_service.client.DeviceClient;
import com.shuku.usage_service.client.UserClient;
import com.shuku.usage_service.model.DeviceEnergy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UsageService {

    @Value("${influx.bucket}")
    private String influxBucket;

    @Value("${influx.org}")
    private String influxOrg;

    private InfluxDBClient influxDBClient;
    private DeviceClient deviceClient;
    private UserClient userClient;

    private KafkaTemplate<String, AlertingEvent> kafkaTemplate;

    public UsageService(InfluxDBClient influxDBClient,DeviceClient deviceClient,UserClient userClient,KafkaTemplate<String,AlertingEvent> kafkaTemplate){
        this.influxDBClient= influxDBClient;
        this.deviceClient=deviceClient;
        this.userClient=userClient;
        this.kafkaTemplate=kafkaTemplate;
    }

    @KafkaListener(topics = "energy-usage", groupId = "usage-service")
    public void energyUsageEvent(EnergyUsageEvent energyUsageEvent){
        //log.info("Received energy usage event {}", energyUsageEvent );

        Point point = Point.measurement("energy-usage")
                .addTag("deviceId",String.valueOf(energyUsageEvent.deviceId()))
                .addField("energyConsumed",energyUsageEvent.energyConsumed())
                .time(energyUsageEvent.timestamp(), WritePrecision.MS);
        influxDBClient.getWriteApiBlocking().writePoint(influxBucket,influxOrg,point);


    }


    @Scheduled(cron = "*/10 * * * * *")
    public void aggregateDeviceEnergyUsage(){
        final Instant now = Instant.now();
        final Instant oneHourAgo = now.minusSeconds(3600);

        String fluxQuery = String.format("""
                from(bucket: "%s")
                    |> range(start: time(v: "%s"), stop: time(v: "%s"))
                    |> filter(fn: (r) => r._measurement == "energy-usage" and r._field == "energyConsumed")
                    |> group(columns: ["deviceId"])
                    |> sum()
                """,influxBucket,oneHourAgo.toString(),now);
        QueryApi queryApi = influxDBClient.getQueryApi();
        List<FluxTable> tables = queryApi.query(fluxQuery, influxOrg);
        List<DeviceEnergy> deviceEnergies = new ArrayList<>();

        for(FluxTable table : tables){
            for(FluxRecord record: table.getRecords()){
                String deviceIdStr =(String) record.getValueByKey("deviceId");
                Double energyConsumed = record.getValueByKey("_value") instanceof Number ?
                        ((Number) record.getValueByKey("_value")).doubleValue() : 0.0;
                deviceEnergies.add(
                        DeviceEnergy.builder()
                                .deviceId(Long.valueOf(deviceIdStr))
                                .energyConsumed(energyConsumed)
                                .build()
                );
            }
        }
        log.info("Aggregated the device energies over the past hour: {}", deviceEnergies);
        for(DeviceEnergy deviceEnergy: deviceEnergies){
            try{



                final DeviceDto deviceResponse = deviceClient.getDeviceById(deviceEnergy.getDeviceId());

                if(deviceResponse == null || deviceResponse.id() == null){
                    log.warn("Device not found for ID: {}",deviceEnergy.getDeviceId());
                    continue;
                    }

                deviceEnergy.setUserId(deviceResponse.userId());

            }catch (Exception exe){
                log.warn("Failed to fetch device for Id: {}", deviceEnergy.getDeviceId());
                continue;
            }

        }
        //remove devices with no userId
        deviceEnergies.removeIf(de -> de.getUserId()==null);
        // map users to all their devices
        Map<Long,List<DeviceEnergy>> userDeviceEnergyMap = new HashMap<>();

        for (DeviceEnergy deviceEnergy: deviceEnergies){
            if(!userDeviceEnergyMap.containsKey(deviceEnergy.getUserId())){
                userDeviceEnergyMap.put(deviceEnergy.getUserId(),new ArrayList<>());
            }
            userDeviceEnergyMap.get(deviceEnergy.getUserId()).add(deviceEnergy);
        }
        log.info("User-Device Energy Map: {}", userDeviceEnergyMap);


        // get users energy consumption thresholds
        List<Long> userIds = new ArrayList<>(userDeviceEnergyMap.keySet());
        final Map<Long,Double> userThresholdMap = new HashMap<>();
        final Map<Long,String> userEmailMap = new HashMap<>();

        for(final Long userId : userIds){
            try{
                UserDto user = userClient.getUserById(userId);
                if (user == null || user.id() == null || !user.alerting()){
                    log.warn("User not found or alerting disabled for ID: {}",userId);
                    continue;
                }
                userThresholdMap.put(userId,user.energyAlertingThreshold());
                userEmailMap.put(userId,user.email());

            }catch (Exception exe){
                log.warn("Failed to fetch user for ID: {}", userId);

            }

        }
        log.info("User threshold Map: {}", userThresholdMap);

        //check thresholds against the aggregated usage

        //users to alert
        final List<Long> alertedUsers = new ArrayList<>(userThresholdMap.keySet());
        for(Long userId: alertedUsers){
            final Double threshold = userThresholdMap.get(userId);
            final List<DeviceEnergy> devices = userDeviceEnergyMap.get(userId);

            Double totalConsumption = 0.0;
            for (DeviceEnergy deviceEnergy: devices){
                Double value = deviceEnergy.getEnergyConsumed();
                totalConsumption += value;
            }

            if(totalConsumption > threshold){
                log.info("ALERT: USER ID {} has exceeded the energy threshold! Total Consumption: {}, Threshold: {}",userId,totalConsumption,threshold);


                final AlertingEvent alertingEvent = AlertingEvent.builder()
                .userId(userId)
                        .message("Energy consumption threshold exceeded")
                        .threshold(threshold)
                        .energyConsumed(totalConsumption)
                        .email(userEmailMap.get(userId))
                        .build();
                log.info("seeding the Alerting Event: {}", alertingEvent);
                kafkaTemplate.send("energy-alerts",alertingEvent);

            }else{
                log.info("USER ID {} is within the energy threshold. Total consumption: {}, Threshold: {}",userId,totalConsumption,threshold);
            }

        }

    }
}
