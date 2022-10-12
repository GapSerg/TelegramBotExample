package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

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
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE_OF_OFFER;

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
        log.info("Change the first date of offer, changed first date: {} and token : {} by user : {}",
                firstDate, token, callbackQuery.getFrom().getUserName());
        Request changeOfferRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        changeOfferRequest.setFirstDate(firstDate);
        changeOfferRequest = requestService.updateRequest(changeOfferRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = String.format(getOffersView(changeOfferRequest), changeOfferRequest.getFirstDate());
        return createEditMessageForSecondDate(callbackQuery, firstDate, textMessage
                , CHANGE_SECOND_DATE_OF_OFFER.ordinal(), RETURN_TO_CHANGE_OF_OFFER.ordinal(), changeOfferRequest.getToken().getId());

    }
}
