package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.model.entity.Offer;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
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
public class FinishDateDriverCallbackType implements CallbackType {

    private final RequestService requestService;

    public FinishDateDriverCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.FINISH_CHOSE_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Get callback : {} type with token : {} by user : {}",
                FINISH_CHOSE_DATE_DRIVER,token,callbackQuery.getFrom().getUserName());
        Request driverRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        List<Offer> passengers = requestService.findPassengersByRequestData(driverRequest);
        String textMessage =getCompletedMessageAnswer(passengers, driverRequest, CREATED_REQUEST);
        return createSendMessageWithDoubleCheckOffer(callbackQuery, textMessage, CHECK_DRIVER_REQUEST.ordinal(),
                CANCEL_DRIVER_REQUEST.ordinal(), driverRequest.getToken().getId());
    }
}
