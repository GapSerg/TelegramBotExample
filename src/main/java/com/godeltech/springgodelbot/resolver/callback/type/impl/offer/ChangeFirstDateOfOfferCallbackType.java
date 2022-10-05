package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_FIRST_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_SECOND_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_SECOND_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.INCORRECT_FIRST_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeFirstDateOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CHANGE_FIRST_DATE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate firstDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Change the first date of offer, changed first date: {} and token : {}", firstDate,token);
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(),token );
        if (validFirstDate(firstDate)) {
            changeOfferRequest.setFirstDate(firstDate);
            return createEditMessageForSecondDate(callbackQuery, firstDate, CHOOSE_THE_SECOND_DATE
                    , CHANGE_SECOND_DATE_OF_OFFER.ordinal(),token );
        } else {
            log.info("Incorrect first date, return to the first date");
            return createEditMessageTextForFirstDateWithIncorrectDate(callbackQuery, CHANGE_FIRST_DATE_OF_OFFER.ordinal(),
                    INCORRECT_FIRST_DATE, firstDate,token );
        }
    }
}
