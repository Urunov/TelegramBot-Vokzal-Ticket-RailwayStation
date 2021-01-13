package com.urunov.telgbot.botapi;

import com.urunov.telgbot.botapi.handlers.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BotStateContext {

    private Map<BotState, InputMessageHandler>  messageHandlers = new HashMap<>();

    public BotStateContext(List<InputMessageHandler> messageHandlers) {
        messageHandlers.forEach(handler -> this.messageHandlers.put(handler.getHandlerName(), handler));
    }

    public SendMessage processInputMessage(BotState currentState, Message message){
        InputMessageHandler currentMessageHandler = findMessageHandler(currentState);
        return currentMessageHandler.handle(message);
    }

    private InputMessageHandler findMessageHandler(BotState currentState){
        if(isTrainSearchState(currentState)){
            return messageHandlers.get(BotState.TRAINS_SEARCH);
        }

        if (isStationSearchState(currentState)){
            return messageHandlers.get(BotState.STATIONS_SEARCH);
        }

        return messageHandlers.get(currentState);
    }

    private boolean isTrainSearchState(BotState currentState)
    {
        switch (currentState){
            case ASK_STATION_ARRIVAL:
            case TRAINS_SEARCH:
            case DATE_DEPART_RECEIVED:
            case ASK_SATATION_NAMEPART:
            case ASK_DATE_DEPART:
            case TRAINS_SEARCH_STARTED:
            case TRAINS_SEARCH_FINISH:
            case TRAIN_INFO_RESPONCE_AWAITING:

                return true;
            default:
                return false;
        }
    }

    private boolean isStationSearchState(BotState currentState){
        switch (currentState){
            case SHOW_STATIONS_BOOK_MENU:
            case ASK_SATATION_NAMEPART:
            case STATION_NAMEPART_RECEIVED:
            case STATIONS_SEARCH:
                return true;
            default:
                return false;
        }
    }
}
