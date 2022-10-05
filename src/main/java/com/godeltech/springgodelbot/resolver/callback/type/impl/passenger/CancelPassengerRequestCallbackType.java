package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.MessageService;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;

@Component
@Slf4j
public class CancelPassengerRequestCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final MessageService messageService;

    public CancelPassengerRequestCallbackType(RequestService requestService,
                                              @Lazy TudaSudaTelegramBot tudaSudaTelegramBot, MessageService messageService) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.messageService = messageService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.CANCEL_PASSENGER_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback : {} with token : {}", Callbacks.CANCEL_PASSENGER_REQUEST, token);
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage(),token );
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        tudaSudaTelegramBot.deleteMessages(callbackQuery.getMessage().getChatId(), passengerRequest.getMessages());
        messageService.deleteToken(token);
        return getStartMenu(callbackQuery.getMessage().getChatId(), "You can start from the beginning");
    }
}
