package com.urunov.telgbot.botapi;

import com.urunov.telgbot.botapi.handlers.callbackquery.CallbackQueryFacade;
import com.urunov.telgbot.cache.UserDataCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@Slf4j
public class TelegramFacade {

    private UserDataCache userDataCache;
    private BotStateContext botStateContext;
    private CallbackQueryFacade callbackQueryFacade;

    public TelegramFacade(UserDataCache userDataCache, BotStateContext botStateContext, CallbackQueryFacade callbackQueryFacade) {
        this.userDataCache = userDataCache;
        this.botStateContext = botStateContext;
        this.callbackQueryFacade = callbackQueryFacade;
    }

    public SendMessage handleUpdate(Update update){
        SendMessage replyMessage = null;

        if (update.hasCallbackQuery()) {
            log.info("New callbackQuery from User: {} with data: {}",  update.getCallbackQuery().getFrom().getUserName(),
                    update.getCallbackQuery().getData());
            return callbackQueryFacade.pro(update.getCallbackQuery());
        }

        Message message = update.getMessage();
        if(message !=null && message.hasText()){
            log.info("New message from User: {} with data: {}",
                    message.getFrom().getUserName(), message.getChatId(), message.getText());
            replyMessage = handleInputMessage(message);
        }
        return replyMessage;
    }

    private SendMessage handleInputMessage(Message message){
        String inputMsg = message.getText();
        int userId = message.getFrom.getId();
        BotState botState;
        SendMessage replyMessage;

        switch (inputMsg){
            case "Find train": // Найти поезда
                botState = BotState.TRAINS_SEARCH;
                break;
            case "My subscription": // Мои подписки
                botState = BotState.SHOW_SUBSCRIPTIONS_MENU;
                break;
            case "Directory of stations": // Справочник станций
                botState = BotState.STATIONS_SEARCH;
                break;
            case "Help": // Помощь
                botState = BotState.SHOW_HELP_MENU;
                break;
            default:
                botState = userDataCache.getUsersCurrentBotState(userId);
                break;

        }

        userDataCache.setUsersCurrentBotState(userId, botState);

        replyMessage = botStateContext.processInputMessage(botState, message);

        return replyMessage;
    }
}
