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
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageTextForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_FIRST_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelDateDriverCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CANCEL_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} type with token : {} by user : {}",
                CANCEL_DATE_DRIVER, token,callbackQuery.getFrom().getUserName());
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        Request driverRequest =requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        return canceledDate.equals(driverRequest.getFirstDate()) ?
                getEditMessageWithCanceledFirstDate(callbackQuery, driverRequest,canceledDate) :
                getEditMessageWithCanceledSecondDate(callbackQuery, driverRequest);
    }

    private BotApiMethod getEditMessageWithCanceledSecondDate(CallbackQuery callbackQuery, Request driverRequest) {
        driverRequest.setSecondDate(null);
        String textMessage = String.format(CHOSEN_FIRST_DATE, driverRequest.getActivity(), getCurrentRoute(driverRequest.getCities()),
                driverRequest.getFirstDate());
        driverRequest = requestService.updateRequest(driverRequest,callbackQuery.getMessage(),callbackQuery.getFrom() );
        return createEditMessageForSecondDate(callbackQuery, driverRequest.getFirstDate(),
                textMessage, SECOND_DATE_DRIVER.ordinal(), CANCEL_DATE_DRIVER.ordinal(), driverRequest.getToken().getId());
    }

    private BotApiMethod getEditMessageWithCanceledFirstDate(CallbackQuery callbackQuery, Request driverRequest,
                                                              LocalDate canceledDate) {
        if (driverRequest.getSecondDate() != null) {
            driverRequest.setFirstDate(driverRequest.getSecondDate());
            driverRequest.setSecondDate(null);
            driverRequest = requestService.updateRequest(driverRequest,callbackQuery.getMessage(),callbackQuery.getFrom());
            String textMessage = String.format(CHOSEN_FIRST_DATE, driverRequest.getActivity(), getCurrentRoute(driverRequest.getCities()),
                    driverRequest.getFirstDate());
            return createEditMessageForSecondDate(callbackQuery, driverRequest.getFirstDate(),
                    textMessage, SECOND_DATE_DRIVER.ordinal(), CANCEL_DATE_DRIVER.ordinal(), driverRequest.getToken().getId());
        }
        driverRequest.setFirstDate(null);
        String textMessage = String.format(CHOOSE_THE_FIRST_DATE, driverRequest.getActivity(), getCurrentRoute(driverRequest.getCities()));
        driverRequest = requestService.updateRequest(driverRequest,callbackQuery.getMessage(),callbackQuery.getFrom() );
        return createEditMessageTextForFirstDate(callbackQuery, FIRST_DATE_DRIVER.ordinal(),
                CANCEL_DRIVER_REQUEST.ordinal(), textMessage, canceledDate, driverRequest.getToken().getId());
    }
}
