package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

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
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.getCurrentSuitableActivities;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.getDatesInf;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE_DRIVER;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstDateDriverCallbackType implements CallbackType {


    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return FIRST_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate firstDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with first date :{} with token: {} by user : {}",
                FIRST_DATE_DRIVER, firstDate, token, callbackQuery.getFrom().getUserName());
        Request driverRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        driverRequest.setFirstDate(firstDate);
        driverRequest = requestService.updateRequest(driverRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = String.format(CHOSEN_FIRST_DATE_DRIVER, driverRequest.getActivity().getTextMessage(),
                getCurrentRoute(driverRequest.getCities()),getCurrentSuitableActivities(driverRequest.getSuitableActivities()),
                getDatesInf(driverRequest.getFirstDate()));
        return createEditMessageForSecondDate(callbackQuery, driverRequest.getFirstDate(),
                textMessage, SECOND_DATE_DRIVER.ordinal(), CANCEL_DRIVER_REQUEST.ordinal(), driverRequest.getToken().getId());
    }
}
