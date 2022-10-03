package com.godeltech.springgodelbot.config;


import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.service.RestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.godeltech.springgodelbot.util.BotMenu;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
@Slf4j
public class BotInitializer {

    private final BotProp botProp;
    private final RestService restService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    @SneakyThrows
    @EventListener({ContextRefreshedEvent.class})
    public void init() {
        log.info("Set web hook with path :{}", botProp.getWebHookPath());
        restService.setWebHook(botProp.getWebHookPath(), botProp.getToken());

        try {
            tudaSudaTelegramBot.execute(new SetMyCommands(BotMenu.getCommands(), new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            throw new UnknownCommandException();
        }
    }
}


