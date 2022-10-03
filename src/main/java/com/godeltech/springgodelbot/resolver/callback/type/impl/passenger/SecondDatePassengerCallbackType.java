package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@Slf4j
public class SecondDatePassengerCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public SecondDatePassengerCallbackType(RequestService requestService,
                                           @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public String getCallbackName() {
        return SECOND_DATE_PASSENGER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        var secondDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got Second date consumer callback type with second date :{}", secondDate);
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        return validSecondDate(passengerRequest.getFirstDate(), secondDate) ?
                createSendMessageWithValidSecondDate(callbackQuery, secondDate, passengerRequest) :
                createEditMessageForSecondDate(callbackQuery, passengerRequest.getFirstDate(),
                        INCORRECT_SECOND_DATE, SECOND_DATE_PASSENGER.name(), secondDate);
    }

    private SendMessage createSendMessageWithValidSecondDate(CallbackQuery callbackQuery,
                                                             LocalDate secondDate, PassengerRequest passengerRequest) {
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        passengerRequest.setSecondDate(secondDate);
        tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_DATE,passengerRequest.getFirstDate(),
                passengerRequest.getSecondDate()));
        var offers = requestService.findDriversByRequestData(passengerRequest);
        return createSendMessageWithDoubleCheckOffer(callbackQuery,offers,CHECK_PASSENGER_REQUEST,CANCEL_PASSENGER_REQUEST);
    }


}
