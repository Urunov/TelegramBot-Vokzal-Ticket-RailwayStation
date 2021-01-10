package com.urunov.telgbot.repository;

import com.urunov.telgbot.model.UserTicketsSubsctiption;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserTicketsSubscriptionMongoRepo extends MongoRepository<UserTicketsSubsctiption, String> {

    List<UserTicketsSubsctiption> findByChatId(long chatId);

    List<UserTicketsSubsctiption> findByChatIdAndTrainNumberAndDateDepart(long chatId, String trainNumber, String dateDepart);
}
