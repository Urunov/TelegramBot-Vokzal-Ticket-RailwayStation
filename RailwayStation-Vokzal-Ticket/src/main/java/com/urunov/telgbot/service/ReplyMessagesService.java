package com.urunov.telgbot.service;

import com.vdurmont.emoji.Emoji;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import java.util.Objects;

@Service
public class ReplyMessagesService {

    private LocaleMessageService localeMessageService;

    public ReplyMessagesService(LocaleMessageService localeMessageService) {
        this.localeMessageService = localeMessageService;
    }

    public SendMessage getReplyMessage(long charId, String replyMessage){
        return new SendMessage(charId, localeMessageService.getMessage(replyMessage));
    }

    public SendMessage getReplyMessage(long chatId, String replyMessage, Object ... args){
        return new SendMessage(chatId, localeMessageService.getMessage(replyMessage, args));
    }

    public SendMessage getSuccessReplyMessage(long chatId, String replyMessage){
        return new SendMessage(chatId, getEmojiReplyText(replyMessage, Emoji.SUCESS_MARK));
    }

    public SendMessage getWarningReplyMessage(long chatId, String replyMessage){
        return new SendMessage(chatId, getEmojiReplyText(replyMessage, Emoji.NOTIFICATION_MARK_FAILED));
    }

    public String getReplyText(String replyText){
        return localeMessageService.getMessage(replyText);
    }

    public String getEmojiReplyText(String replyText, Emoji emoji){
        return localeMessageService.getMessage(replyText, emoji);
    }

    public String getReplyText(String s, String catType, int freeSeats, int minimalPrice) {
    }
}

