package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_DRIVER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SECOND_DATE_DRIVER;
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.getCurrentSuitableActivities;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_SECOND_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_SECOND_DATE_DRIVER;

@Component
@Slf4j
public class SecondDateDriverCallbackType implements CallbackType {

    private final RequestService requestService;


    public SecondDateDriverCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return SECOND_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate chosenDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with second date :{} and token : {} by user : {}",
                SECOND_DATE_DRIVER, chosenDate, token, callbackQuery.getFrom().getUserName());
        Request driverRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        setDatesToRequest(chosenDate, driverRequest);
        driverRequest = requestService.updateRequest(driverRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = String.format(CHOSEN_SECOND_DATE_DRIVER, driverRequest.getActivity().getTextMessage(),
                getCurrentRoute(driverRequest.getCities()), getCurrentSuitableActivities(driverRequest.getSuitableActivities())
                , getDatesInf(driverRequest.getFirstDate(), driverRequest.getSecondDate()));
        return createEditMessageForSecondDate(callbackQuery, driverRequest.getFirstDate(), textMessage,
                SECOND_DATE_DRIVER.ordinal(), CANCEL_DRIVER_REQUEST.ordinal(), driverRequest.getSecondDate(),
                driverRequest.getToken().getId());
    }


}
