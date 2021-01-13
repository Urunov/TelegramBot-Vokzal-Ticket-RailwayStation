package com.urunov.telgbot.service;

import com.urunov.telgbot.model.TrainStation;
import com.urunov.telgbot.utils.Emojis;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Slf4j
public class StationBookService {
    @Value("${stationcodeservice.requesttemplate}")
    private String stationSearchTemplate;
    private RestTemplate restTemplate;
    private StationsDataCache stationsCache;
    private ReplyMessagesService messagesService;

    public StationBookService(RestTemplate restTemplate, StationsDataCache stationsCache, ReplyMessagesService messagesService) {
        this.restTemplate = restTemplate;
        this.stationsCache = stationsCache;
        this.messagesService = messagesService;
    }

    public SendMessage processStationNamePart(long chatId, String stationNamePartParam) {
        String searchedStationName = stationNamePartParam.toUpperCase();

        Optional<String> optionalStationName = stationsCache.getStationName(searchedStationName);
        if (optionalStationName.isPresent()) {
            return messagesService.getReplyMessage(chatId, "reply.stationBook.stationFound", Emojis.SUCCESS_MARK, optionalStationName.get());
        }

        List<TrainStation> trainStations = sendStationSearchRequest(searchedStationName);

        List<String> foundedStationNames = trainStations.stream().
                map(TrainStation::getStationName).filter(stationName -> stationName.contains(searchedStationName)).collect(Collectors.toList());

        if (foundedStationNames.isEmpty()) {
            return messagesService.getReplyMessage(chatId, "reply.stationBookMenu.stationNotFound");
        }

        StringBuilder stationsList = new StringBuilder();
        foundedStationNames.forEach(stationName -> stationsList.append(stationName).append("\n"));

        return messagesService.getReplyMessage(chatId, "reply.stationBook.stationsFound", Emojis.SUCCESS_MARK, stationsList.toString());

    }
