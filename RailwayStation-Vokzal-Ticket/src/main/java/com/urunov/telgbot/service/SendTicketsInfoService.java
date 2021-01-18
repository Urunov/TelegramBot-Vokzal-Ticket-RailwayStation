package com.urunov.telgbot.service;

import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.botapi.handlers.callbackquery.CallbackQueryType;
import com.urunov.telgbot.cache.UserDataCache;
import com.urunov.telgbot.model.Car;
import com.urunov.telgbot.model.Train;
import com.urunov.telgbot.utils.Emojis;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SendTicketsInfoService {

    private VakzalTelgramBot vakzalTelgramBot;
    private CarsProcessingService carsProcessingService;
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public SendTicketsInfoService (CarsProcessingService carsProcessingService,
                                   UserDataCache userDataCache,
                                   ReplyMessagesService messagesService,
                                   @Lazy VakzalTelgramBot vakzalTelgramBot) {
        this.carsProcessingService = carsProcessingService;
        this.userDataCache = userDataCache;
        this.messagesService = messagesService;
        this.vakzalTelgramBot = vakzalTelgramBot;
    }

    public void sendTrainTicketsInfo(long chatId, List<Train> trainList){
        for(Train train: trainList){
            StringBuilder carsInfo = new StringBuilder();
            List<Car> carsWithMinimalPrice = carsProcessingService.filterCarsWithMinimumPrice((List<Car>) train.getAvailableCars());
            train.setAvailableCars((Car) carsWithMinimalPrice);

            for (Car car: carsWithMinimalPrice)
            {
                carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                        car.getCatType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String trainTicketsInfoMessage = messagesService.getReplyText("reply.trainSearch.trainInfo",
                    Emojis.TRAIN, train.getNumber(), train.getBrand(), train.getStationDepart(),
                    train.getDateDepart(), train.getTimeDepart(), train.getStationArrival(),
                    train.getDateArrival(), Emojis.TIME_IN_WAY, train.getTimeInWay(), carsInfo);
            String trainsInfoData = String.format("%s%|%|%s", CallbackQueryType.SUBSCRIBE,
                    train.getNumber(), train.getDateDepart());

            vakzalTelgramBot.sendInlineKeyBoardMessage(chatId, trainTicketsInfoMessage, "Subscribe", trainsInfoData);
        }

        userDataCache.saveSearchFoundedTrains(chatId, trainList);
    }
}
