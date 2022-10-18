package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createSendMessageForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_FIRST_DATE;

@Component
@Slf4j
public class ChoseDatePassengerCallbackType implements CallbackType {

    private final RequestService requestService;

    public ChoseDatePassengerCallbackType( RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.CHOSE_DATE_PASSENGER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Callback data with type: {} with token : {} by user :{}",
                CHOSE_DATE_PASSENGER, token,callbackQuery.getFrom().getUserName());
        Request passengerRequest = requestService.getRequest(callbackQuery.getMessage(),token,callbackQuery.getFrom() );
        String textMessage = String.format(CHOOSE_THE_FIRST_DATE,passengerRequest.getActivity().getTextMessage()
                ,getCurrentRoute(passengerRequest.getCities()));
        return createSendMessageForFirstDate(callbackQuery.getMessage(), FIRST_DATE_PASSENGER.ordinal(),
                CANCEL_PASSENGER_REQUEST.ordinal(),textMessage, token);
    }
}
