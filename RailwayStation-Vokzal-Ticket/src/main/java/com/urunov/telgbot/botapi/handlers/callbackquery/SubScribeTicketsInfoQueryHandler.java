package com.urunov.telgbot.botapi.handlers.callbackquery;

import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.cache.UserDataCache;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Component
public class SubScribeTicketsInfoQueryHandler implements CallbackQueryHandler {

   private static final CallbackQueryType HANDLER_QUERY_TYPE = CallbackQueryType.SUBSCRIBE;
   private UserTicketsSubsriptionService subsriptionService;
   private ParseQueryDataService parseQueryDataService;
   private ReplyMessageService messageService;
   private UserDataCache userDataCache;
   private VakzalTelgramBot vakzalTelgramBot;


    public SubScribeTicketsInfoQueryHandler(UserTicketsSubsriptionService subsriptionService, ParseQueryDataService parseQueryDataService, ReplyMessageService messageService,
                                            UserDataCache userDataCache, VakzalTelgramBot vakzalTelgramBot) {
        this.subsriptionService = subsriptionService;
        this.parseQueryDataService = parseQueryDataService;
        this.messageService = messageService;
        this.userDataCache = userDataCache;
        this.vakzalTelgramBot = vakzalTelgramBot;
    }

    @Override
    public SendMessage handleCallbackQuery(CallbackQuery callbackQuery) {
        return null;
    }

    @Override
    public CallbackQueryType getHandlerQueryType() {
        return null;
    }
}
