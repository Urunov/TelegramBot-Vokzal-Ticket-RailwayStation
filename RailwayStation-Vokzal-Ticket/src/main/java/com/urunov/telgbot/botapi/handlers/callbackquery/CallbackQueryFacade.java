package com.urunov.telgbot.botapi.handlers.callbackquery;

import com.urunov.telgbot.service.ReplyMessagesService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Optional;

@Component
public class CallbackQueryFacade {

    private ReplyMessagesService messagesService;
    private List<CallbackQueryHandler> callbackQueryHandlers;

    public CallbackQueryFacade(ReplyMessagesService messagesService, List<CallbackQueryHandler> callbackQueryHandlers) {
        this.messagesService = messagesService;
        this.callbackQueryHandlers = callbackQueryHandlers;

    }

    public SendMessage processCallbackQuery(CallbackQuery usersQuery){
        CallbackQueryType usersQueryType = CallbackQueryType.valueOf(usersQuery.getData().split("\\|")[0]);

        Optional<CallbackQueryHandler> queryHandler = callbackQueryHandlers.stream().findAny()
                .filter(callbackQuery -> callbackQuery.getHandlerQueryType().equals(usersQueryType)).stream().findFirst();

        return queryHandler.map(handler -> handler.handleCallbackQuery(usersQuery))
                .orElse(messagesService.getWarningReplyMessage(usersQuery.getMessage().getChatId(), "reply.query.failed"));
    }
}

