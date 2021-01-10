package com.urunov.telgbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.ApiContext;
import org.telegram.telegrambots.meta.TelegramBotsApi;

@SpringBootApplication
@EnableScheduling
public class TelgbotApplication {

    public static void main(String[] args) {

        ApiContextInitializer.init();

        SpringApplication.run(TelgbotApplication.class, args);



        // Set up Http proxy
//        DefaultBotOptions botOptions = ApiContext.getInstance(DefaultBotOptions.class);
//
//        botOptions.setProxyType(DefaultBotOptions.ProxyType.SOCKS5);
//        botOptions.setProxyHost("localhost");
//        botOptions.setProxyPort(9150);
//
//
//        MySuperBot mySuperBot = new MySuperBot(botOptions);
//
//        TelegramBotsApi botsApi = new TelegramBotsApi();
//
//        try {
//            botsApi.registerBot(mySuperBot);
//
//        } catch (Exception e)
//        {
//            e.printStackTrace();
//        }
    }

}
