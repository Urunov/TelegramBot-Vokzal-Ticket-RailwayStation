package com.urunov.telgbot.botapi.handlers.callbackquery;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum UserChatButtonStatus {

    SUBSCRIBED("Subscribed"),UNSUBSCRIBED("Unsubscribed");

    private String buttonStatus;

    @Override
    public String toString(){
        return buttonStatus;
    }
}
