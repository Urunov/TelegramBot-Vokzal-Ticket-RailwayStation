package com.urunov.telgbot.botapi.handlers;

import com.urunov.telgbot.botapi.BotState;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface InputMessageHandler {

    SendMessage handle(Message message);

    BotState getHandlerName();
}
