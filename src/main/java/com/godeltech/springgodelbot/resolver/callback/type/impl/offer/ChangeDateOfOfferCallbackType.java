package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_DATES;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE_OF_OFFER;

@Component
@Slf4j
public class ChangeDateOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    public ChangeDateOfOfferCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return CHANGE_DATE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} type with token: {}", CHANGE_DATE_OF_OFFER, token);
        Request changeOfferRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        if (callbackQuery.getFrom().getUserName() == null)
            throw new UserAuthorizationException(User.class, "username", null, callbackQuery.getMessage(), false);
        log.info("Change date of offer with id:{}, with token :{}",
                changeOfferRequest.getOfferId(), token);
        if (changeOfferRequest.getSecondDate() == null) {
            String textMessage = String.format(CHOSEN_FIRST_DATE_OF_OFFER, changeOfferRequest.getFirstDate());
            return createEditMessageForSecondDate(callbackQuery, changeOfferRequest.getFirstDate(),
                    textMessage, CHANGE_SECOND_DATE_OF_OFFER.ordinal(), RETURN_TO_CHANGE_OF_OFFER.ordinal(), token);
        } else {
            return createEditMessageForSecondDate(callbackQuery, changeOfferRequest.getFirstDate(),
                    String.format(CHOSEN_DATES, changeOfferRequest.getFirstDate(), changeOfferRequest.getSecondDate()),
                    CHANGE_SECOND_DATE_OF_OFFER.ordinal(),
                    RETURN_TO_CHANGE_OF_OFFER.ordinal(), changeOfferRequest.getSecondDate(), changeOfferRequest.getToken().getId());
        }
    }
}
