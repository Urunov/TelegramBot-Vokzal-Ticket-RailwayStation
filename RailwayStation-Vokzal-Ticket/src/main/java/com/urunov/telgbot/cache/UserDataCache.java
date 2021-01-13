package com.urunov.telgbot.cache;

import com.urunov.telgbot.botapi.BotState;
import com.urunov.telgbot.botapi.handlers.trainsearch.TrainSearchRequestData;
import com.urunov.telgbot.model.Train;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserDataCache implements DataCache{

    private Map<Integer, BotState> usersBotState = new HashMap<>();
    private Map<Integer, TrainSearchRequestData> trainSearchUsersData = new HashMap<>();
    private Map<Long, List<Train>> searchFoundedTrains = new HashMap<>();


    @Override
    public void setUsersCurrentBotState(int userId, BotState botState) {
        usersBotState.put(userId, botState);
    }

    @Override
    public BotState getUsersCurrentBotState(int userId) {
        BotState botState = usersBotState.get(userId);
        if(botState == null){
            botState = BotState.SHOW_MAIN_MENU;
        }
        return botState;
    }

    @Override
    public TrainSearchRequestData getUserTrainSearchData(int userId) {
        return null;
    }

    @Override
    public void saveSearchFoundedTrains(long chatId, List<Train> foundTrains) {
            searchFoundedTrains.put(chatId, foundTrains);
    }

    @Override
    public void saveTrainSearchData(int userId, TrainSearchRequestData trainSearchRequestData) {
        trainSearchUsersData.put(userId, trainSearchRequestData);
    }

    @Override
    public List<Train> getSearchFoundedTrains(long chatId) {
       List<Train> foundedTrains = searchFoundedTrains.get(chatId);
        return Objects.isNull(foundedTrains) ? Collections.emptyList(): foundedTrains;
    }


}
