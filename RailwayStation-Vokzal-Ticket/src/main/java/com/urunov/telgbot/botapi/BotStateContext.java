package com.urunov.telgbot.botapi;

import com.urunov.telgbot.botapi.handlers.InputMessageHandler;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class BotStateContext {

    private Map<BotState, InputMessageHandler>  messageHandlers = new HashMap<>();


}
