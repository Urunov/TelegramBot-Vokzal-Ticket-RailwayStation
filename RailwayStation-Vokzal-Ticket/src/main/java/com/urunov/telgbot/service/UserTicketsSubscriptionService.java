package com.urunov.telgbot.service;


import com.urunov.telgbot.model.UserTicketsSubscription;
import com.urunov.telgbot.repository.UserTicketsSubscriptionMongoRepo;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserTicketsSubscriptionService {

    private UserTicketsSubscriptionMongoRepo subscriptionMongoRepo;

    private UserTicketsSubscriptionService(UserTicketsSubscriptionMongoRepo repository){
        this.subscriptionMongoRepo = repository;
    }

    public List<UserTicketsSubscription> getAllSubscriptions(){
        return subscriptionMongoRepo.findAll();
    }

    public void saveUserSubscription(UserTicketsSubscription usersSubscription){
        subscriptionMongoRepo.save(usersSubscription);
    }

    public void deleteUserSubscription(String subscriptionID){
        subscriptionMongoRepo.deleteById(subscriptionID);
    }

    public boolean hasTicketsSubscription(UserTicketsSubscription userSubscription){
        return subscriptionMongoRepo.findByChatIdAndTrainNumberAndDateDepart(userSubscription.getChatId(),
                userSubscription.getTrainNumber(), userSubscription.getDateDepart()).size() > 0;
    }

    public Optional<UserTicketsSubscription> getUsersSubscriptionById(String subscriptionID){
        return subscriptionMongoRepo.findById(subscriptionID);
    }

    public List<UserTicketsSubscription> getUsersSubscriptions(long chatId){
        return subscriptionMongoRepo.findByChatId(chatId);
    }
}
