package com.urunov.telgbot.cache;

import java.util.Optional;
import java.util.stream.Stream;

public interface StationCache {
    Stream<String> getStationName(String stationNameParam);

    Optional<Integer> getStationCode(String stationNameParam);

    void addStationToCache(String stationName, int stationCode);
}
