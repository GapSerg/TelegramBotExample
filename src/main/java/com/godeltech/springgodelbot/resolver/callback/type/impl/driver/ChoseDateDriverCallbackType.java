package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.getCurrentSuitableActivities;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createSendMessageForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_FIRST_DATE_DRIVER;

@Component
@Slf4j
public class ChoseDateDriverCallbackType implements CallbackType {

    private final RequestService requestService;

    public ChoseDateDriverCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return CHOSE_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Callback data with type: {} with token : {} by user :{}",
                CHOSE_DATE_DRIVER, token, callbackQuery.getFrom());
        Request driverRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        String textMessage = String.format(CHOOSE_THE_FIRST_DATE_DRIVER, driverRequest.getActivity().getTextMessage()
                , getCurrentRoute(driverRequest.getCities()), getCurrentSuitableActivities(driverRequest.getSuitableActivities()));
        return createSendMessageForFirstDate(callbackQuery.getMessage(), FIRST_DATE_DRIVER.ordinal(),
                CANCEL_DRIVER_REQUEST.ordinal(), textMessage, driverRequest.getToken().getId());
    }
}
