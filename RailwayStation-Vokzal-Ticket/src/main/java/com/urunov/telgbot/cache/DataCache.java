package com.urunov.telgbot.cache;

import com.urunov.telgbot.botapi.BotState;
import com.urunov.telgbot.botapi.handlers.trainsearch.TrainSearchRequestData;
import com.urunov.telgbot.model.Train;

import java.util.List;

public interface DataCache {

    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    void saveSearchFoundedTrains(long chatId, List<Train> foundTrains);

    void saveTrainSearchData(int userId, TrainSearchRequestData trainSearchRequestData);

    List<Train> getSearchFoundedTrains(long chatId);

    TrainSearchRequestData getUserTrainSearchData(int userId);


}
