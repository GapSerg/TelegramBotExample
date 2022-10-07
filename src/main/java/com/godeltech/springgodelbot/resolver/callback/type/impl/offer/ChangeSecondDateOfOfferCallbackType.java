package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_SECOND_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.RETURN_TO_CHANGE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeSecondDateOfOfferCallbackType implements CallbackType {


    private final RequestService requestService;
    private final TokenService tokenService;

    @Override
    public Integer getCallbackName() {
        return CHANGE_SECOND_DATE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate chosenDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Change date of offer with date :{} with token: {}", chosenDate, token);
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(), token);
        setDatesToRequest(chosenDate, changeOfferRequest);
        return createEditMessageForSecondDate(callbackQuery, changeOfferRequest.getFirstDate(),
                String.format(CHOSEN_DATES,changeOfferRequest.getFirstDate(),changeOfferRequest.getSecondDate()),
                CHANGE_SECOND_DATE_OF_OFFER.ordinal(),
                RETURN_TO_CHANGE_OF_OFFER.ordinal(), changeOfferRequest.getSecondDate(), token);
    }




}
