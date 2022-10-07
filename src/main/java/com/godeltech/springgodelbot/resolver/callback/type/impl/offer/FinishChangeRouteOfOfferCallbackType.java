package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.FINISH_CHANGING_ROUTE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishChangeRouteOfOfferCallbackType implements CallbackType {


    private final RequestService requestService;
    private final TokenService tokenService;

    @Override
    public Integer getCallbackName() {
        return FINISH_CHANGING_ROUTE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback type :{}, with token:{}", FINISH_CHANGING_ROUTE_OF_OFFER, token);
        ChangeOfferRequest changeOfferRequest =
                requestService.getChangeOfferRequest(callbackQuery.getMessage(),token );
        requestService.updateRouteOfOffer(changeOfferRequest,token );
        List<? extends Request> requests = changeOfferRequest.getActivity() == Activity.DRIVER ?
               requestService.findPassengersByRequestData(changeOfferRequest) :
               requestService.findDriversByRequestData(changeOfferRequest);
        tokenService.deleteToken(token);
        String textMessage = getCompletedMessageAnswer(requests, changeOfferRequest, ROUTE_CHANGED);
        return getAvailableOffersList(requests,callbackQuery, textMessage,token);
    }


}
