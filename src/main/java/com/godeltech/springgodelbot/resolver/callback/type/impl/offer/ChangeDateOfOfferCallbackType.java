package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_FIRST_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForFirstDate;

@Component
@Slf4j
public class ChangeDateOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    public ChangeDateOfOfferCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public String getCallbackName() {
        return CHANGE_DATE_OF_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        ChangeDriverRequest changeDriverRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage());
        if (callbackQuery.getFrom().getUserName() == null)
            throw new UserAuthorizationException(UserDto.class, "username", null, callbackQuery.getMessage(),false );
        log.info("Change date of offer with id:{}, by user :{}",
                changeDriverRequest.getOfferId(), callbackQuery.getFrom().getUserName());
        return createEditMessageForFirstDate(callbackQuery, CHANGE_FIRST_DATE_OF_OFFER.name(),
                "You previous date is " + changeDriverRequest.getFirstDate() + " - " + changeDriverRequest.getSecondDate());
    }
}
