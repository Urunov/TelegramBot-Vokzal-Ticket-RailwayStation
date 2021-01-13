package com.urunov.telgbot.repository;

import com.urunov.telgbot.model.UserTicketsSubscription;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserTicketsSubscriptionMongoRepo extends MongoRepository<UserTicketsSubscription, String> {

    List<UserTicketsSubscription> findByChatId(long chatId);

    List<UserTicketsSubscription> findByChatIdAndTrainNumberAndDateDepart(long chatId, String trainNumber, String dateDepart);
}
