package com.urunov.telgbot.botapi.handlers.menu;

import com.urunov.telgbot.botapi.BotState;
import com.urunov.telgbot.botapi.handlers.InputMessageHandler;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;


@Component
public class HelpMenuHandler implements InputMessageHandler {

    private MainMenuService mainMenuService;
    private ReplyMessageService messageService;

    public HelpMenuHandler(MainMenuService mainMenuService, ReplyMessageService messageService) {
        this.mainMenuService = mainMenuService;
        this.messageService = messageService;
    }

    @Override
    public SendMessage handle(Message message) {
        return mainMenuService.getMainMenuMessage(message.getChatId(),
                messageService.getEmojiReplyText("reply.helpMenu.welcomeMessage", Emojis.HELP_MENU_WELCOME));
    }

    @Override
    public BotState getHandlerName() {
        return BotState.SHOW_HELP_MENU;
    }
}
