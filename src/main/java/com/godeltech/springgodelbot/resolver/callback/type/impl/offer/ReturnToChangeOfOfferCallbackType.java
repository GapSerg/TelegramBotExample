package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.RETURN_TO_CHANGE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ReturnToChangeOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return RETURN_TO_CHANGE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} type with token : {} by user : {}"
                ,RETURN_TO_CHANGE_OF_OFFER,token,callbackQuery.getFrom().getUserName());
        Request changeOfferRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom());
        ChangeOfferRequest request =
                requestService.refreshChangeOfferRequest(changeOfferRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        String textMessage = getOffersView(request);
        return  getEditTextMessageForOffer(callbackQuery, token,  request,textMessage);
    }
}
