package com.urunov.telgbot.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.model.Train;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
@Getter
@Setter
public class TrainTicketsGetInfoService {

    @Value("${trainTicketsGetInfoService.ridRequestTemplate}")
    private String trainInfoRidRequestTemplate;

    @Value("${trainTicketGetInfoService.trainInfoRequestTemplate}")
    private String trainInfoRequestTemplate;

    private static final String URI_PARAM_STATION_DEPART_CODE = "STATION_DEPART_CODE";
    private static final String URI_PARAM_STATION_ARRIVAL_CODE = "STATION_ARRIVAL_CODE";
    private static final String URI_PARAM_DATE_DEPART = "DATE_DEPART";
    private static final String TRAIN_DATE_OUT_OF_DATE_MESSAGE = "находится за пределами периода/data out message";


    private final ObjectMapper objectMapper = new ObjectMapper();
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("dd.MM.yyyy");
    private final RestTemplate restTemplate;
    private final ReplyMessagesService messagesService;
    private final VakzalTelgramBot vakzalTelgramBot;

    public TrainTicketsGetInfoService(String trainInfoRidRequestTemplate, String trainInfoRequestTemplate,
                                      RestTemplate restTemplate, ReplyMessagesService messagesService, VakzalTelgramBot vakzalTelgramBot) {
        this.trainInfoRidRequestTemplate = trainInfoRidRequestTemplate;
        this.trainInfoRequestTemplate = trainInfoRequestTemplate;
        this.restTemplate = restTemplate;
        this.messagesService = messagesService;
        this.vakzalTelgramBot = vakzalTelgramBot;
    }

    public List<Train> getTrainTicketList(long chatId, int stationDepartCode, int stationArrivalCode, Date dateDepart)
    {
        List<Train> trainList;
        String dateDepartStr = dateFormatter.format(dateDepart);
        Map<String, String> urlParams = new HashMap<>();

        urlParams.put(URI_PARAM_STATION_DEPART_CODE, String.valueOf(stationDepartCode));
        urlParams.put(URI_PARAM_STATION_ARRIVAL_CODE, String.valueOf(stationArrivalCode));
        urlParams.put(URI_PARAM_DATE_DEPART, dateDepartStr);

        Map<String, HttpHeaders> ridAndHttpHeaders = sendRidRequest(chatId, urlParams);
        if(ridAndHttpHeaders.isEmpty())
            return Collections.emptyList();

        String ridValue = ridAndHttpHeaders.keySet().iterator().next();
        HttpHeaders httpHeaders = ridAndHttpHeaders.get(ridValue);
        List<String> cookies = httpHeaders.get("Set-Cookie");

        if(cookies == null){
            telgbotApplication.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.query.failed"));
            return Collections.emptyList();
        }

        HttpHeaders trainInfoRequestHeaders = getDataRequestHeaders(cookies);
        String trainInfoResponseBody = sendTrainInfoJsonRequest(ridValue, trainInfoRequestHeaders);

        trainList = parseResponseBody(trainInfoResponseBody);

        return trainList;
    }

    private Map<String, HttpHeaders> sendRidRequest(long chatId, Map<String, String> urlParams)
    {
        ResponseEntity<String> passRzdResponse = restTemplate.getForEntity(trainInfoRequestTemplate, String.class, urlParams);

        String jsonRespBody = passRzdResponse.getBody();

        if(isResponseBodyHasNoTrains(jsonRespBody)){
            telgbotApplication.sendMessage(messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.dateOutOfBoundError"));
            return Collections.emptyMap();
        }

        Optional<String> parsedRID = parseRID(jsonRespBody);
        if(parsedRID.isEmpty()){
            return Collections.emptyMap();
        }

        return Collections.singletonMap(parsedRID.get(), passRzdResponse.getHeaders());
    }


}
