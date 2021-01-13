package com.urunov.telgbot.controller;

import com.urunov.telgbot.botapi.VakzalTelgramBot;
import com.urunov.telgbot.model.UserTicketsSubscription;
import com.urunov.telgbot.service.UserTicketsSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Slf4j
@RestController
public class WebHookController {

    private final VakzalTelgramBot vakzalTelgramBot;
    private final UserTicketsSubscriptionService userTicketsSubscriptionService;


    public WebHookController(VakzalTelgramBot vakzalTelgramBot, UserTicketsSubscriptionService userTicketsSubscriptionService) {
        this.vakzalTelgramBot = vakzalTelgramBot;
        this.userTicketsSubscriptionService = userTicketsSubscriptionService;
    }

    @RequestMapping(value = "/", method = RequestMethod.POST)
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return vakzalTelgramBot.onWebhookUpdateReceived(update);
    }


    @GetMapping(value = "/subscription", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserTicketsSubscription> index(){
        List<UserTicketsSubscription> userTicketsSubscriptions = userTicketsSubscriptionService.getAllSubscriptions();

        return userTicketsSubscriptions;
    }
}
