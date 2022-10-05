package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;

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
        log.info("Got {} type with token: {}",CHANGE_DATE_OF_OFFER,token);
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(),token );
        if (callbackQuery.getFrom().getUserName() == null)
            throw new UserAuthorizationException(UserDto.class, "username", null, callbackQuery.getMessage(),false );
        log.info("Change date of offer with id:{}, with token :{}",
                changeOfferRequest.getOfferId(), token);
        return createEditMessageForFirstDate(callbackQuery, CHANGE_FIRST_DATE_OF_OFFER.ordinal(),RETURN_TO_CHANGE_OF_OFFER.ordinal(),
                "You previous date is " + changeOfferRequest.getFirstDate() + " - " + changeOfferRequest.getSecondDate(),token );
    }
}
