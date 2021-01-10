package com.urunov.telgbot.botapi.handlers.callbackquery;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class UnsubscribeTicketsInfoQueryHandler implements CallbackQueryHandler{

    private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.UNSUBSCRIBE;
    private UserTicketsSubscriptionService subscriptionService;
    private ParseQueryDataService parseService;
    private ReplyMessageService messageService;
    private


    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        return null;
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return null;
    }
}
