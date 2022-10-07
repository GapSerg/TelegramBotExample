package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.ConstantUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageTextForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_FIRST_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDatePassengerCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CANCEL_DATE_PASSENGER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} type with token : {}", CANCEL_DATE_PASSENGER, token);
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage(), token);
        return canceledDate.equals(passengerRequest.getFirstDate()) ?
                getEditMessageWithCanceledFirstDate(callbackQuery, passengerRequest, token, canceledDate) :
                getEditMessageWithCanceledSecondDate(callbackQuery, passengerRequest, token);
    }


    private BotApiMethod getEditMessageWithCanceledSecondDate(CallbackQuery callbackQuery, PassengerRequest passengerRequest,
                                                              String token) {
        passengerRequest.setSecondDate(null);
        String textMessage = String.format(CHOSEN_FIRST_DATE, passengerRequest.getActivity(), getCurrentRoute(passengerRequest.getCities()),
                passengerRequest.getFirstDate());
        return createEditMessageForSecondDate(callbackQuery, passengerRequest.getFirstDate(),
                textMessage, SECOND_DATE_PASSENGER.ordinal(), CANCEL_DATE_PASSENGER.ordinal(), token);
    }

    private BotApiMethod getEditMessageWithCanceledFirstDate(CallbackQuery callbackQuery, PassengerRequest passengerRequest,
                                                             String token, LocalDate canceledDate) {
        if (passengerRequest.getSecondDate() != null) {
            passengerRequest.setFirstDate(passengerRequest.getSecondDate());
            passengerRequest.setSecondDate(null);
            String textMessage = String.format(CHOSEN_FIRST_DATE, passengerRequest.getActivity(), getCurrentRoute(passengerRequest.getCities()),
                    passengerRequest.getFirstDate());
            return createEditMessageForSecondDate(callbackQuery, passengerRequest.getFirstDate(),
                    textMessage, SECOND_DATE_PASSENGER.ordinal(), CANCEL_DATE_PASSENGER.ordinal(), token);
        }
        passengerRequest.setFirstDate(null);
        String textMessage = String.format(CHOOSE_THE_FIRST_DATE, passengerRequest.getActivity(), getCurrentRoute(passengerRequest.getCities()));
        return createEditMessageTextForFirstDate(callbackQuery, FIRST_DATE_PASSENGER.ordinal(),
                CANCEL_PASSENGER_REQUEST.ordinal(), textMessage, canceledDate, token);
    }
}
