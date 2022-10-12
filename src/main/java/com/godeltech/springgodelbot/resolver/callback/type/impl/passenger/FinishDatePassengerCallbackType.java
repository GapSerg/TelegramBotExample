package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.model.entity.Offer;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.CREATED_REQUEST;

@Component
@Slf4j
public class FinishDatePassengerCallbackType implements CallbackType {
    private final RequestService requestService;

    public FinishDatePassengerCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return FINISH_CHOSE_DATE_PASSENGER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Callback : {} type with token : {} by user : {}",
                FINISH_CHOSE_DATE_PASSENGER, token, callbackQuery.getFrom().getUserName());
        Request passengerRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());

        List<Offer> drivers = requestService.findDriversByRequestData(passengerRequest);

        String textMessage = getCompletedMessageAnswer(drivers, passengerRequest, CREATED_REQUEST);
        return createSendMessageWithDoubleCheckOffer(callbackQuery, textMessage, CHECK_PASSENGER_REQUEST.ordinal(),
                CANCEL_PASSENGER_REQUEST.ordinal(), passengerRequest.getToken().getId());
    }
}
