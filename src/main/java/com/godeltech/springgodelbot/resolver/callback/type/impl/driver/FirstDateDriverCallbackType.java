package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.FIRST_DATE_DRIVER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SECOND_DATE_DRIVER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_SECOND_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.INCORRECT_FIRST_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstDateDriverCallbackType implements CallbackType {


    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return Callbacks.FIRST_DATE_DRIVER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {

        LocalDate firstDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got First Date Supplier Callback type with first date :{} by user: {}", firstDate
                , callbackQuery.getFrom().getUserName());
        DriverRequest supplerRequest = requestService.getDriverRequest(callbackQuery.getMessage());
        return validFirstDate(firstDate) ?
                getEditMessageTextWithValidFirstDate(callbackQuery, firstDate, supplerRequest) :
                createEditMessageTextForFirstDateWithIncorrectDate(callbackQuery, FIRST_DATE_DRIVER.name(),
                        INCORRECT_FIRST_DATE, firstDate);
    }

    private EditMessageText getEditMessageTextWithValidFirstDate(CallbackQuery callbackQuery,
                                                                 LocalDate firstDate, DriverRequest driverRequest) {
        driverRequest.setFirstDate(firstDate);
        driverRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        return createEditMessageForSecondDate(callbackQuery, firstDate,
                CHOOSE_THE_SECOND_DATE, SECOND_DATE_DRIVER.name());
    }
}
