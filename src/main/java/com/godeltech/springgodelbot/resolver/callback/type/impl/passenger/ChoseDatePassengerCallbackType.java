package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHOSE_DATE_PASSENGER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.FIRST_DATE_PASSENGER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createSendMessageForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.SELECTED_ROUTE;

@Component
@Slf4j
public class ChoseDatePassengerCallbackType implements CallbackType {

    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final RequestService requestService;

    public ChoseDatePassengerCallbackType(@Lazy TudaSudaTelegramBot tudaSudaTelegramBot,
                                          RequestService requestService) {
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.requestService = requestService;
    }

    @Override
    public String getCallbackName() {
        return Callbacks.CHOSE_DATE_PASSENGER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Callback data with type: {} by user : {}", CHOSE_DATE_PASSENGER, callbackQuery.getFrom().getUserName());
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        String selectedRoute = getCurrentRoute(passengerRequest.getCities());
        tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(SELECTED_ROUTE, selectedRoute));
        return createSendMessageForFirstDate(callbackQuery.getMessage().getChatId(), FIRST_DATE_PASSENGER.name(),
                CHOOSE_THE_FIRST_DATE);
    }
}
