package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.Offer;
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
        log.info("Got {} callback type with token : {} by user : {}",
                Callbacks.FINISH_DATE_OFFER, token,callbackQuery.getFrom().getUserName());
        ChangeOfferRequest changeOfferRequest =
                (ChangeOfferRequest) requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        requestService.updateDates(changeOfferRequest, token,callbackQuery.getMessage(),callbackQuery.getFrom() );
        List<Offer> requests = changeOfferRequest.getActivity() == Activity.DRIVER ?
                requestService.findPassengersByRequestData(changeOfferRequest) :
                requestService.findDriversByRequestData(changeOfferRequest);
        return updateDatesOfSupplierAndGetStartMenu(callbackQuery,
                requests, changeOfferRequest);

    }

    private BotApiMethod updateDatesOfSupplierAndGetStartMenu(CallbackQuery callbackQuery, List<Offer> requests,
                                                              ChangeOfferRequest changeOfferRequest) {
        String textMessage = getCompletedMessageAnswer(requests, changeOfferRequest,DATES_WERE_CHANGED);
        return getAvailableOffersList(requests, callbackQuery, textMessage, changeOfferRequest.getToken().getId());
    }


}
