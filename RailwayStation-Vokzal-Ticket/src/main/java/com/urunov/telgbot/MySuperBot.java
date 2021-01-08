package com.urunov.telgbot;

import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class MySuperBot extends TelegramLongPollingBot {

    private static final String TOKEN = "1476257968:AAGEriyXAIynfhDx8Dn0DirJYPMZ6zobRck";
    private static final String USERNAME = "vakzal_bot";

    public MySuperBot(DefaultBotOptions options)
    {
        super(options);
    }

    @Override
    public String getBotUsername() {
        return USERNAME;
    }

    @Override
    public String getBotToken() {
        return TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        if(update.getMessage()!=null && update.getMessage().hasText())
        {
            long chat_id = update.getUpdateId().getChatId();

            try {
                execute(new SendMessage(chat_id, " Hi " + update.getMessage().hasText()));
            } catch (TelegramApiException e){
                e.printStackTrace();
            }
        }
    }
}
