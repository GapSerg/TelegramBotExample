package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_SECOND_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.INCORRECT_FIRST_DATE;

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
        log.info("Got {} callback type with first date :{} with token: {}", FIRST_DATE_DRIVER, firstDate
                , token);
        DriverRequest supplerRequest = requestService.getDriverRequest(callbackQuery.getMessage(), token);
        return validFirstDate(firstDate) ?
                getEditMessageTextWithValidFirstDate(callbackQuery, firstDate, supplerRequest, token) :
                createEditMessageTextForFirstDateWithIncorrectDate(callbackQuery, FIRST_DATE_DRIVER.ordinal(),
                        CANCEL_DRIVER_REQUEST.ordinal(),
                        INCORRECT_FIRST_DATE, firstDate, token);
    }

    private EditMessageText getEditMessageTextWithValidFirstDate(CallbackQuery callbackQuery,
                                                                 LocalDate firstDate, DriverRequest driverRequest, String token) {
        driverRequest.setFirstDate(firstDate);
        driverRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        return createEditMessageForSecondDate(callbackQuery, firstDate,
                CHOOSE_THE_SECOND_DATE, SECOND_DATE_DRIVER.ordinal(), CANCEL_DRIVER_REQUEST.ordinal(), token);
    }
}
