package com.urunov.telgbot.service;

import lombok.Value;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class LocaleMessageService {

   private Locale locale;
   private final MessageSource messageSource;

   public LocaleMessageService(@Value("${localeTag}") String localeTag, MessageSource messageSource)
   {
       this.messageSource = messageSource;
       this.locale = Locale.forLanguageTag(localeTag);
   }

   public String getMessage(String message){
       return messageSource.getMessage(message, null, locale);
   }

   public String getMessage(String message, Object... args){
       return messageSource.getMessage(message, args, locale);
   }

}
