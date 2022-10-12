package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE;

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
        log.info("Got {} type with first date :{} with token: {} by user : {}",
                FIRST_DATE_PASSENGER, firstDate, token,callbackQuery.getFrom().getUserName());
        Request passengerRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        passengerRequest.setFirstDate(firstDate);
        passengerRequest= requestService.updateRequest(passengerRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        String textMessage = String.format(CHOSEN_FIRST_DATE, passengerRequest.getActivity(),
                getCurrentRoute(passengerRequest.getCities()),passengerRequest.getFirstDate());
        return createEditMessageForSecondDate(callbackQuery, passengerRequest.getFirstDate(),
                textMessage, SECOND_DATE_PASSENGER.ordinal(), CANCEL_PASSENGER_REQUEST.ordinal(), token);
    }
}
