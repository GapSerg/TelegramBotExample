package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstDatePassengerCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return FIRST_DATE_PASSENGER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {

        var firstDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got First Date Supplier Callback type with first date :{} by user: {}", firstDate
                , callbackQuery.getFrom().getUserName());
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        return validFirstDate(firstDate) ?
                getEditMessageTextWithValidFirstDate(callbackQuery, firstDate, passengerRequest) :
                createEditMessageTextForFirstDateWithIncorrectDate(callbackQuery,
                        FIRST_DATE_PASSENGER.name(), INCORRECT_FIRST_DATE, firstDate);

    }

    private EditMessageText getEditMessageTextWithValidFirstDate(CallbackQuery callbackQuery, LocalDate firstDate, PassengerRequest passengerRequest) {
        passengerRequest.setFirstDate(firstDate);
        return createEditMessageForSecondDate(callbackQuery, firstDate,
                CHOOSE_THE_SECOND_DATE, SECOND_DATE_PASSENGER.name());
    }


}
