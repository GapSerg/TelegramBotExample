package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_PASSENGER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SECOND_DATE_PASSENGER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.setDatesToRequest;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_SECOND_DATE;

@Component
@Slf4j
public class SecondDatePassengerCallbackType implements CallbackType {
    private final RequestService requestService;

    public SecondDatePassengerCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return SECOND_DATE_PASSENGER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate chosenDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with second date :{} and token : {} by user : {}",
                SECOND_DATE_PASSENGER, chosenDate, token, callbackQuery.getFrom().getUserName());
        Request passengerRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        setDatesToRequest(chosenDate, passengerRequest);
        passengerRequest= requestService.updateRequest(passengerRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        String textMessage = String.format(CHOSEN_SECOND_DATE, passengerRequest.getActivity(), getCurrentRoute(passengerRequest.getCities()),
                passengerRequest.getFirstDate(), passengerRequest.getSecondDate());
        return createEditMessageForSecondDate(callbackQuery, passengerRequest.getFirstDate(), textMessage,
                SECOND_DATE_PASSENGER.ordinal(), CANCEL_PASSENGER_REQUEST.ordinal(), passengerRequest.getSecondDate(), token);
    }


}
