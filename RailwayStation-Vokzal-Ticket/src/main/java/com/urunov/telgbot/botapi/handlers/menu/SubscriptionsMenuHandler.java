package com.urunov.telgbot.botapi.handlers.menu;

import com.urunov.telgbot.botapi.BotState;
import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.botapi.handlers.InputMessageHandler;
import com.urunov.telgbot.botapi.handlers.callbackquery.CallbackQueryType;
import com.urunov.telgbot.cache.UserDataCache;
import com.urunov.telgbot.model.Car;
import com.urunov.telgbot.model.UserTicketsSubscription;
import com.urunov.telgbot.service.ReplyMessagesService;
import com.urunov.telgbot.service.UserTicketsSubscriptionService;
import com.urunov.telgbot.utils.Emojis;
import org.springframework.context.annotation.Lazy;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public class SubscriptionsMenuHandler implements InputMessageHandler {

    private UserTicketsSubscriptionService subscribeService;
    private VakzalTelgramBot telegramBot;
    private UserDataCache userDataCache;
    private ReplyMessagesService messagesService;

    public SubscriptionsMenuHandler(UserTicketsSubscriptionService subscribeService,
                                    UserDataCache userDataCache,
                                    ReplyMessagesService messagesService,
                                    @Lazy VakzalTelgramBot telegramBot) {
        this.subscribeService = subscribeService;
        this.messagesService = messagesService;
        this.userDataCache = userDataCache;
        this.telegramBot = telegramBot;
    }

    @Override
    public SendMessage handle(Message message) {
        List<UserTicketsSubscription> usersSubscriptions = subscribeService.getUsersSubscriptions(message.getChatId());

        if (usersSubscriptions.isEmpty()) {
            userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);
            return messagesService.getReplyMessage(message.getChatId(), "reply.subscriptions.userHasNoSubscriptions");
        }

        for (UserTicketsSubscription subscription : usersSubscriptions) {
            StringBuilder carsInfo = new StringBuilder();
            List<Car> cars = subscription.getSubscribedCars();

            for (Car car : cars) {
                carsInfo.append(messagesService.getReplyText("subscription.carsTicketsInfo",
                        car.getCatType(), car.getFreeSeats(), car.getMinimalPrice()));
            }

            String subscriptionInfo = messagesService.getReplyText("subscriptionMenu.trainTicketsInfo",
                    Emojis.TRAIN, subscription.getTrainNumber(), subscription.getTrainName(),
                    subscription.getStationDepart(), subscription.getTimeDepart(), subscription.getStationArrival(),
                    subscription.getTimeArrival(), Emojis.TIME_DEPART, subscription.getDateDepart(),
                    subscription.getDateArrival(), carsInfo);

            //Посылаем кнопку "Отписаться" с ID подписки
            String unsubscribeData = String.format("%s|%s", CallbackQueryType.UNSUBSCRIBE, subscription.getId());
            telegramBot.sendInlineKeyBoardMessage(message.getChatId(), subscriptionInfo, "Отписаться", unsubscribeData);
        }

        userDataCache.setUsersCurrentBotState(message.getFrom().getId(), BotState.SHOW_MAIN_MENU);

        return messagesService.getSuccessReplyMessage(message.getChatId(), "reply.subscriptions.listLoaded");
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_SUBSCRIPTIONS_MENU;
    }

}
