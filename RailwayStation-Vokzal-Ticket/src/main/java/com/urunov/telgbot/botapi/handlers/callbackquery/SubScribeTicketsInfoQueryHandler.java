package com.urunov.telgbot.botapi.handlers.callbackquery;

import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.cache.UserDataCache;
import com.urunov.telgbot.model.Car;
import com.urunov.telgbot.model.Train;
import com.urunov.telgbot.model.UserTicketsSubscription;
import com.urunov.telgbot.service.ParseQueryDataService;
import com.urunov.telgbot.service.ReplyMessagesService;
import com.urunov.telgbot.service.UserTicketsSubscriptionService;
import com.urunov.telgbot.utils.Emojis;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Optional;

@Component
public class SubScribeTicketsInfoQueryHandler implements CallbackQueryHandler {

   private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.SUBSCRIBE;
   private UserTicketsSubscriptionService subsriptionService;
   private ParseQueryDataService parseQueryDataService;
   private ReplyMessagesService messageService;
   private UserDataCache userDataCache;
   private VakzalTelgramBot vakzalTelgramBot;


    public SubScribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subsriptionService, ParseQueryDataService parseQueryDataService,
                                            ReplyMessagesService messageService, UserDataCache userDataCache, VakzalTelgramBot vakzalTelgramBot) {
        this.subsriptionService = subsriptionService;
        this.parseQueryDataService = parseQueryDataService;
        this.messageService = messageService;
        this.userDataCache = userDataCache;
        this.vakzalTelgramBot = vakzalTelgramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {

       final long chatId = callbackQuery.getMessage().getChatId();
       final String trainNumber = parseQueryDataService.parseTrainNumberFromSubscribeQuery(callbackQuery);
       final String dateDepart = parseQueryDataService.parseDateDepartFromSubscribeQuery(callbackQuery);

       Optional<UserTicketsSubscription> userTicketsSubscription = parseQueryData(callbackQuery);
       if(userTicketsSubscription.isEmpty()){
           return messageService.getWarningReplyMessage(chatId, "reply.query.searchAgain");
       }

       UserTicketsSubscription userTickets = userTicketsSubscription.get();
       if(subsriptionService.hasTicketsSubscription(userTickets)){
           return messageService.getWarningReplyMessage(chatId, "reply.query.train.userHasSubscription");
       }

       subsriptionService.saveUserSubscription(userTickets);

       vakzalTelgramBot.sendChangedInlineButtonText(callbackQuery,
               String.format("%s %s", Emojis.SUCCESS_SUBSCRIBED, UserChatButtonStatus.SUBSCRIBED), CallbackQueryType.QUERY_PROCESSED.name());

       return messageService.getReplyMessage(chatId, "reply.query.train.subscribed", trainNumber, dateDepart);
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }

    private Optional<UserTicketsSubscription> parseQueryData(CallbackQuery usersQuery) {
        List<Train> foundedTrains = userDataCache.getSearchFoundedTrains(usersQuery.getMessage().getChatId());
        final long chatId = usersQuery.getMessage().getChatId();

        final String trainNumber = parseQueryDataService.parseTrainNumberFromSubscribeQuery(usersQuery);
        final String dateDepart = parseQueryDataService.parseDateDepartFromSubscribeQuery(usersQuery);

        Optional<Train> queriedTrainOptional = foundedTrains.stream().
                filter(train -> train.getNumber().equals(trainNumber) && train.getDateDepart().equals(dateDepart)).
                findFirst();

        if (queriedTrainOptional.isEmpty()) {
            return Optional.empty();
        }

        Train queriedTrain = queriedTrainOptional.get();
        final String trainName = queriedTrain.getBrand();
        final String stationDepart = queriedTrain.getStationDepart();
        final String stationArrival = queriedTrain.getStationArrival();
        final String dateArrival = queriedTrain.getDateArrival();
        final String timeDepart = queriedTrain.getTimeDepart();
        final String timeArrival = queriedTrain.getTimeArrival();
        final List<Car> availableCars = (List<Car>) queriedTrain.getAvailableCars();

        return Optional.of(new UserTicketsSubscription(chatId, trainNumber, trainName, stationDepart, stationArrival, dateDepart, dateArrival, timeDepart, timeArrival, availableCars));
    }


}
