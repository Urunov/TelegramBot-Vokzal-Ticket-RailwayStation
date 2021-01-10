package com.urunov.telgbot.cache;

import com.urunov.telgbot.botapi.BotState;
import com.urunov.telgbot.model.Train;

import java.util.List;

public interface DataCache {

    void setUsersCurrentBotState(int userId, BotState botState);

    BotState getUsersCurrentBotState(int userId);

    void saveTrainSearchData(int userId, TrainSearchRequestData trainSearchData);

    TrainSearchRequestData getUserTrainSearchData(int userId);

    void saveSearchFoundedTrains(long chatId, List<Train> foundTrains);

    List<Train> getSearchFoundedTrains(long chatId);

}
