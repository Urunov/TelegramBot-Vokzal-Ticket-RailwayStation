package com.urunov.telgbot.service;

import com.urunov.telgbot.utils.Emojis;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class ReplyMessagesService {

    private LocaleMessageService localeMessageService;


    public ReplyMessagesService(LocaleMessageService messageService) {
        this.localeMessageService = messageService;
    }

    public SendMessage getReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, localeMessageService.getMessage(replyMessage));
    }

    public SendMessage getReplyMessage(long chatId, String replyMessage, Object... args) {
        return new SendMessage(chatId, localeMessageService.getMessage(replyMessage, args));
    }


    public SendMessage getSuccessReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, getEmojiReplyText(replyMessage, Emojis.SUCCESS_MARK));
    }


    public SendMessage getWarningReplyMessage(long chatId, String replyMessage) {
        return new SendMessage(chatId, getEmojiReplyText(replyMessage, Emojis.NOTIFICATION_MARK_FAILED));
    }

    public String getReplyText(String replyText) {
        return localeMessageService.getMessage(replyText);
    }

    public String getReplyText(String replyText, Object... args) {
        return localeMessageService.getMessage(replyText, args);
    }

    public String getEmojiReplyText(String replyText, Emojis emoji) {
        return localeMessageService.getMessage(replyText, emoji);
    }
}

