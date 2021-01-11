package com.urunov.telgbot.service;

import com.urunov.telgbot.botapi.handlers.callbackquery.CallbackQueryType;
import com.urunov.telgbot.cache.UserDataCache;
import com.urunov.telgbot.model.Car;
import com.urunov.telgbot.model.Train;
import com.vdurmont.emoji.Emoji;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.List;

@Service
public class SendTicketsInfoService {

    private TelegramBotsApi telegramBotsApi;
    private CarsProcessingService carsProcessingService;
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public SendTicketsInfoService (CarsProcessingService carsProcessingService,
                                   UserDataCache userDataCache,
                                   ReplyMessagesService messagesService,
                                   @Lazy TelegramBotsApi telegramBotsApi) {
        this.carsProcessingService = carsProcessingService;
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.telegramBotsApi = telegramBotsApi;
    }

    public void sendTrainTicketsInfo(long chatId, List<Train> trainList){
        for(Train train: trainList){
            StringBuilder carsInfo = new StringBuilder();
            List<Car> carsWithMinimalPrice = carsProcessingService.filterCarsWithMinimumPrice(train.getAvailableCars());
            train.setAvailableCars(carsWithMinimalPrice);

            for (Car car: carsWithMinimalPrice)
            {
                carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                        car.getCatType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String trainTicketsInfoMessage = messagesService.getReplyText("reply.trainSearch.trainInfo",
                    Emoji.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(),
                    train.getDateDepart(), train.getTimeDepart(), train.getStationArrival(),
                    train.getDateArrival(), Emoji.TIME_IN_WAY, train.getTimeInWay(), carsInfo);
            String trainsInfoData = String.format("%s%|%|%s", CallbackQueryType.SUBSCRIBE,
                    train.getNumber(), train.getDateDepart());

            telegramBotsApi.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, "Subscribe", trainsInfoData);
        }

        userDataCache.saveSearchFoundedTrains(chatId, trainList);
    }
}
