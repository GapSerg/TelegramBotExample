package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.FIRST_DATE_PASSENGER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SECOND_DATE_PASSENGER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_SECOND_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.INCORRECT_FIRST_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstDatePassengerCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return FIRST_DATE_PASSENGER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate firstDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} type with first date :{} with token: {}",FIRST_DATE_PASSENGER, firstDate
                , token);
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage(),token );
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        return validFirstDate(firstDate) ?
                getEditMessageTextWithValidFirstDate(callbackQuery, firstDate, passengerRequest,token) :
                createEditMessageTextForFirstDateWithIncorrectDate(callbackQuery,
                        FIRST_DATE_PASSENGER.ordinal(), INCORRECT_FIRST_DATE, firstDate,token );

    }

    private EditMessageText getEditMessageTextWithValidFirstDate(CallbackQuery callbackQuery, LocalDate firstDate, PassengerRequest passengerRequest, String token) {
        passengerRequest.setFirstDate(firstDate);
        return createEditMessageForSecondDate(callbackQuery, firstDate,
                CHOOSE_THE_SECOND_DATE, SECOND_DATE_PASSENGER.ordinal(),token );
    }


}
