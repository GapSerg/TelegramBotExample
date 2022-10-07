package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.DriverRequest;
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

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.createSendMessageWithDoubleCheckOffer;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_DATE;

@Component
@Slf4j
public class FinishDatePassengerCallbackType implements CallbackType {
    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final RequestService requestService;

    public FinishDatePassengerCallbackType(@Lazy TudaSudaTelegramBot tudaSudaTelegramBot,
                                           RequestService requestService) {
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.FINISH_CHOSE_DATE_PASSENGER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage(), token);
        tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_DATE, passengerRequest.getFirstDate(),
                passengerRequest.getSecondDate()));
        List<PassengerRequest> passengers = requestService.findPassengersByRequestData(passengerRequest);
        return createSendMessageWithDoubleCheckOffer(callbackQuery, passengers, CHECK_PASSENGER_REQUEST.ordinal(),
                CANCEL_PASSENGER_REQUEST.ordinal(), token);
    }
}
