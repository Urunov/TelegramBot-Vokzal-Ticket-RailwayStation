package com.urunov.telgbot.service;

import com.urunov.telgbot.cache.StationDataCache;
import com.urunov.telgbot.model.TrainStation;
import lombok.AccessLevel;
import lombok.Value;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class StationCodeService {
    @Value("${stationcodeservice.requesttemplate}")
    private String stationCodeRequestTemplate;
    private RestTemplate restTemplate;
    private StationDataCache stationCache;

    public StationCodeService(String stationCodeRequestTemplate, RestTemplate restTemplate) {
        this.stationCodeRequestTemplate = stationCodeRequestTemplate;
        this.restTemplate = restTemplate;
    }

    public int getStationCode(String stationName){
        String stationNameParam = stationName.toUpperCase();

        Optional<Integer> stationCodeOptional = stationCache.getStationCode(stationNameParam);
        if(stationCodeOptional.isPresent())
            return stationCodeOptional.get();
        if(processStationCodeRequest(stationNameParam).isEmpty()){
            return -1;
        }

        return stationCache.getStationCode(stationNameParam).orElse(-1);
    }

    private Optional<TrainStation[]> processStationCodeRequest(String stationNamePart){
        ResponseEntity<TrainStation[]> response = restTemplate.getForEntity(
                stationCodeRequestTemplate, TrainStation[].class, stationNamePart);
        TrainStation[] stations = response.getBody();
        if(stations == null){
            return Optional.empty();
        }

        for(TrainStation station: stations){
            stationCache.addStationToCache(station.getStationName(), station.getStationCode());
        }

        return Optional.of(stations);
    }
}
