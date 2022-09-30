package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.godeltech.springgodelbot.util.BotMenu;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SAVE_PASSENGER_WITHOUT_DESCRIPTION;

@Component
@Slf4j
public class SavePassengerWithoutDescriptionCallbackType implements CallbackType {

    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;


    public SavePassengerWithoutDescriptionCallbackType(RequestService requestService,
                                                       @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public String getCallbackName() {
        return SAVE_PASSENGER_WITHOUT_DESCRIPTION.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got callback with type : {} by user : {}",
                SAVE_PASSENGER_WITHOUT_DESCRIPTION,callbackQuery.getFrom().getUserName());
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        tudaSudaTelegramBot.deleteMessages(callbackQuery.getMessage().getChatId(), passengerRequest.getMessages());
        requestService.savePassenger(passengerRequest);
        return BotMenu.getStartMenu(callbackQuery.getMessage(),"We've successfully save your request");
    }
}
