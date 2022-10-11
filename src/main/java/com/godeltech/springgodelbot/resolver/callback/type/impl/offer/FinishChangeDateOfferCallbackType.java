package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishChangeDateOfferCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.FINISH_DATE_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} callback type with token : {}", Callbacks.FINISH_DATE_OFFER, token);
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(), token);
        requestService.updateDates(changeOfferRequest, token,callbackQuery.getMessage() );
        List<? extends Request> requests = changeOfferRequest.getActivity() == Activity.DRIVER ?
                requestService.findPassengersByRequestData(changeOfferRequest) :
                requestService.findDriversByRequestData(changeOfferRequest);
        return updateDatesOfSupplierAndGetStartMenu(callbackQuery,
                requests, changeOfferRequest, token);

    }

    private BotApiMethod updateDatesOfSupplierAndGetStartMenu(CallbackQuery callbackQuery, List<? extends Request> requests,
                                                              ChangeOfferRequest changeOfferRequest, String token) {
        String textMessage = getCompletedMessageAnswer(requests, changeOfferRequest,DATES_WERE_CHANGED);
        return getAvailableOffersList(requests, callbackQuery, textMessage, token);
    }


}
