package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelFirstDatePassengerCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return CANCEL_FIRST_DATE_PASSENGER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with canceled date :{}", CANCEL_FIRST_DATE_PASSENGER,canceledDate);
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        passengerRequest.setFirstDate(null);
        return CallbackUtil.DateUtil.createEditMessageTextForFirstDate(callbackQuery, FIRST_DATE_PASSENGER.name(),
                "You've canceled the first date", canceledDate);
    }
}
