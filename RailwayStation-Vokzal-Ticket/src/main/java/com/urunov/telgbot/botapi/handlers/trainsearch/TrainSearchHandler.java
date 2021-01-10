package com.urunov.telgbot.botapi.handlers.trainsearch;

import com.urunov.telgbot.botapi.BotState;
import com.urunov.telgbot.botapi.handlers.InputMessageHandler;
import com.urunov.telgbot.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
@Slf4j
public class TrainSearchHandler implements InputMessageHandler {

    private UserDataCache userDataCache;
    private TrainTicketGetInfoService trainTicketGetInfoService;
    private StationCodeService stationCodeService;
    private SendTicketsInfoService sendTicketsInfoService;
    private ReplyMessagesService messagesService;

    public TrainSearchHandler(UserDataCache userDataCache, TrainTicketGetInfoService trainTicketGetInfoService, StationCodeService stationCodeService,
                              SendTicketsInfoService sendTicketsInfoService, ReplyMessagesService messagesService) {
        this.userDataCache = userDataCache;
        this.trainTicketGetInfoService = trainTicketGetInfoService;
        this.stationCodeService = stationCodeService;
        this.sendTicketsInfoService = sendTicketsInfoService;
        this.messagesService = messagesService;
    }

    @Override
    public SendMessage handle(Message message) {

       if(userDataCache.getUsersCurrentBotState(message.getFrom().getId()).equals(BotState.TRAINS_SEARCH)){
           userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.ASK_STATION_DEPART);
       }
        return processUsersInput(message);
    }

    @Override
    public BotState getHandlerName() {
        return BotState.TRAINS_SEARCH;
    }

    private SendMessage processUsersInput(Message inputMsg){
        String usersAnswer = inputMsg.getText();
        int userId = inputMsg.getFrom().getId();
        long chatId = inputMsg.getChatId();
        SendMessage replyUser = messagesService.getWarningReplyMessage(chatId, "reply.trainSearch.tryAgain");
        TrainSearchRequestData requestData = userDataCache.getUserTrainSearchData(userId);

        BotState botState = userDataCache.getUsersCurrentBotState(userId);
        if(botState.equals(BotState.ASK_STATION_DEPART))
        {
            replyToUser = messagesService.getReplyMessage(chatId, "reply.trainSearch.enterStationDepart");
            userDataCache.setUsersCurrentBotState(userId, BotState.ASK_STATION_ARRIVAL);
        }

        if(botState.equals(BotState.ASK_STATION_ARRIVAL))
        {
            int departureStationCode = stationCodeService.getStationCode(usersAnswer);
            if(departureStationCode == -1){
                return messagesService.getWarningReplyMessage(chatId, "reply.trainSerarch.stationNotFound");
            }
        }
    }
}
