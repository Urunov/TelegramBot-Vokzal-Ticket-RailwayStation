package com.urunov.telgbot.cache;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
/*
*  @athour: Urunov Hamdamboy
* */

@Component
@Getter
@Setter
public class StationDataCache implements StationCache {

    private Map<String, Integer> stationCodeCache = new HashMap<>();

    @Override
    public Stream<String> getStationName(String stationNameParam) {
        return stationCodeCache.keySet().stream().filter(stationName -> stationName.equals(stationNameParam));
    }

    @Override
    public Optional<Integer> getStationCode(String stationNameParam) {
        return Optional.ofNullable(stationCodeCache.get(stationNameParam));
    }

    @Override
    public void addStationToCache(String stationName, int stationCode) {
            stationCodeCache.put(stationName, stationCode);
    }
}
