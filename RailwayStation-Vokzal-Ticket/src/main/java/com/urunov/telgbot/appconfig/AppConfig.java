package com.urunov.telgbot.appconfig;

import com.urunov.telgbot.botapi.TelegramFacade;
import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.botconfig.VakzalTelegramBotConfig;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;

@Configuration
public class AppConfig {

    private VakzalTelegramBotConfig botConfig;

    public AppConfig(VakzalTelegramBotConfig vakzalTelegramBotConfig) {
        this.botConfig = vakzalTelegramBotConfig;
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    public MessageSource messageSource(){
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();

        messageSource.setBasename("classpath:messages");
        messageSource.setDefaultEncoding("UTF-8");
        return messageSource;
    }

    @Bean
    public VakzalTelgramBot VakzalTelgramBot(TelegramFacade telegramFacade){
        DefaultBotOptions options = ApiContext
                .getInstance(DefaultBotOptions.class);
        options.setProxyHost(botConfig.getProxyHost());
        options.setProxyPort(botConfig.getProxyPort());
        options.setProxyType(botConfig.getProxyType());

        VakzalTelgramBot vakzalTelgramBot = new VakzalTelgramBot(options, telegramFacade);
        vakzalTelgramBot.setBotUsername(botConfig.getUserName());
        vakzalTelgramBot.setBotToken(vakzalTelgramBot.getBotToken());
        vakzalTelgramBot.setBotPath(vakzalTelgramBot.getBotPath());

        return vakzalTelgramBot;
    }

}
