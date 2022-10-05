package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
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

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.validSecondDate;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.INCORRECT_SECOND_DATE;

@Component
@Slf4j
public class SecondDateDriverCallbackType implements CallbackType {

    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public SecondDateDriverCallbackType(RequestService requestService,
                                        @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public Integer getCallbackName() {
        return SECOND_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate secondDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with second date :{} and token : {}",SECOND_DATE_DRIVER,
                secondDate, token);
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage(),token );
        return validSecondDate(driverRequest.getFirstDate(), secondDate) ?
                getSendMessageWithValidSecondDate(callbackQuery, secondDate, driverRequest,token) :
                createEditMessageForSecondDate(callbackQuery, driverRequest.getFirstDate(), INCORRECT_SECOND_DATE,
                        SECOND_DATE_DRIVER.ordinal(), secondDate, token);
    }

    private SendMessage getSendMessageWithValidSecondDate(CallbackQuery callbackQuery, LocalDate secondDate, DriverRequest driverRequest, String token) {
        driverRequest.setSecondDate(secondDate);
        driverRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_DATE, driverRequest.getFirstDate(),
                driverRequest.getSecondDate()));
        List<PassengerRequest> passengers = requestService.findPassengersByRequestData(driverRequest);

        return createSendMessageWithDoubleCheckOffer(callbackQuery, passengers, CHECK_DRIVER_REQUEST.ordinal(), CANCEL_DRIVER_REQUEST.ordinal(),token);
    }


}
