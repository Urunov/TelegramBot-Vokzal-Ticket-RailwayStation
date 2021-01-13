package com.urunov.telgbot.botapi.handlers.callbackquery;

import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.model.UserTicketsSubscription;
import com.urunov.telgbot.service.ParseQueryDataService;
import com.urunov.telgbot.service.ReplyMessagesService;
import com.urunov.telgbot.service.UserTicketsSubscriptionService;
import com.urunov.telgbot.utils.Emojis;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Optional;

@Component
public class UnsubscribeTicketsInfoQueryHandler implements CallbackQueryHandler{

    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.UNSUBSCRIBE;
    private UserTicketsSubscriptionService subscriptionService;
    private ParseQueryDataService parseService;
    private ReplyMessagesService messageService;
    private VakzalTelgramBot vakzalTelgramBot;

    public UnsubscribeTicketsInfoQueryHandler(UserTicketsSubscriptionService subscriptionService, ParseQueryDataService parseService,
                                              ReplyMessagesService messageService, @Lazy VakzalTelgramBot vakzalTelgramBot) {
        this.subscriptionService = subscriptionService;
        this.parseService = parseService;
        this.messageService = messageService;
        this.vakzalTelgramBot = vakzalTelgramBot;
    }



    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        final long chatId = callbackQuery.getMessage().getChatId();

        final String subscriptionID = parseService.parseSubscriptionIdFromUnsubscribeQuery(callbackQuery);
        Optional<UserTicketsSubscription> optionalUserSubscription = subscriptionService.getUsersSubscriptionById(subscriptionID);
        if (optionalUserSubscription.isEmpty()) {
            return messageService.getWarningReplyMessage(chatId, "reply.query.train.userHasNoSubscription");
        }

        UserTicketsSubscription userSubscription = optionalUserSubscription.get();
        subscriptionService.deleteUserSubscription(subscriptionID);

        vakzalTelgramBot.sendChangedInlineButtonText(callbackQuery,
                String.format("%s %s", Emojis.SUCCESS_UNSUBSCRIBED, UserChatButtonStatus.UNSUBSCRIBED),
                CallbackQueryType.QUERY_PROCESSED.name());


        return messageService.getReplyMessage(chatId, "reply.query.train.unsubscribed", userSubscription.getTrainNumber(), userSubscription.getDateDepart() );
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return HANDLER_QUERY_TYPE;
    }
}
