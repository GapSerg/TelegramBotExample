package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;

@Component
@Slf4j
public class CancelDriverRequestCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public CancelDriverRequestCallbackType(RequestService requestService,
                                           @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public String getCallbackName() {
        return Callbacks.CANCEL_DRIVER_REQUEST.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got callback : {} by user : {}", Callbacks.CANCEL_DRIVER_REQUEST, callbackQuery.getFrom().getUserName());
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage());
        driverRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        tudaSudaTelegramBot.deleteMessages(callbackQuery.getMessage().getChatId(), driverRequest.getMessages());
        return getStartMenu(callbackQuery.getMessage().getChatId(), "You can start from the beginning");
    }
}
