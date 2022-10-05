package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

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
public class SecondDatePassengerCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public SecondDatePassengerCallbackType(RequestService requestService,
                                           @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public Integer getCallbackName() {
        return SECOND_DATE_PASSENGER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate secondDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with second date : {} and token : {}", SECOND_DATE_PASSENGER,secondDate,token);
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage(),token );
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        return validSecondDate(passengerRequest.getFirstDate(), secondDate) ?
                createSendMessageWithValidSecondDate(callbackQuery, secondDate, passengerRequest,token) :
                createEditMessageForSecondDate(callbackQuery, passengerRequest.getFirstDate(),
                        INCORRECT_SECOND_DATE, SECOND_DATE_PASSENGER.ordinal(), secondDate,token );
    }

    private SendMessage createSendMessageWithValidSecondDate(CallbackQuery callbackQuery,
                                                             LocalDate secondDate, PassengerRequest passengerRequest, String token) {
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        passengerRequest.setSecondDate(secondDate);
        tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_DATE, passengerRequest.getFirstDate(),
                passengerRequest.getSecondDate()));
        List<DriverRequest> offers = requestService.findDriversByRequestData(passengerRequest);
        return createSendMessageWithDoubleCheckOffer(callbackQuery, offers, CHECK_PASSENGER_REQUEST.ordinal(), CANCEL_PASSENGER_REQUEST.ordinal(),token );
    }


}
