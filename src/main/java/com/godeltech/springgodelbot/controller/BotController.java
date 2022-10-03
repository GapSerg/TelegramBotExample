package com.godeltech.springgodelbot.controller;

import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;


@Slf4j
@RestController
@RequiredArgsConstructor
public class BotController {

    private final TudaSudaTelegramBot tudaSudaTelegramBot;


    @PostMapping
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {
        return tudaSudaTelegramBot.onWebhookUpdateReceived(update);
    }

    @GetMapping
    public String cameMessage() {
        System.out.println("PRISHLOOOOOOOOOOOOOOOOOOOOOOOOOOO");
        return "prishloopyat'";
    }
}



