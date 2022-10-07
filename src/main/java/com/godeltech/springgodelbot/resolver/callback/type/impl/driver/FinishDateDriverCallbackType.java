package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;
import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_DRIVER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHECK_DRIVER_REQUEST;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.getDatesInf;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@Slf4j
public class FinishDateDriverCallbackType implements CallbackType {

    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final RequestService requestService;

    public FinishDateDriverCallbackType(@Lazy TudaSudaTelegramBot tudaSudaTelegramBot,
                                        RequestService requestService) {
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.FINISH_CHOSE_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage(), token);
//        tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_DATE, driverRequest.getFirstDate(),
//                driverRequest.getSecondDate()));

        List<PassengerRequest> passengers = requestService.findPassengersByRequestData(driverRequest);
        String textMessage =getCompletedMessageAnswer(passengers, driverRequest, CREATED_REQUEST);
        return createSendMessageWithDoubleCheckOffer(callbackQuery, textMessage, CHECK_DRIVER_REQUEST.ordinal(),
                CANCEL_DRIVER_REQUEST.ordinal(), token);
    }
}
