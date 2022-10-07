package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_DRIVER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SECOND_DATE_DRIVER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.setDatesToRequest;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_SECOND_DATE;

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
        log.info("Got {} callback type with second date :{} and token : {}", SECOND_DATE_DRIVER,
                chosenDate, token);
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage(), token);
        setDatesToRequest(chosenDate, driverRequest);

//                getSendMessageWithValidSecondDate(callbackQuery, chosenDate, driverRequest,token) :
        return createEditMessageForSecondDate(callbackQuery, driverRequest.getFirstDate(), CHOSEN_SECOND_DATE,
                SECOND_DATE_DRIVER.ordinal(), CANCEL_DRIVER_REQUEST.ordinal(), driverRequest.getSecondDate(), token);
    }




}
