package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.DriverItem;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.TransferItem;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_CHANGE_OFFER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.FINISH_CHANGING_ROUTE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.DATES_WERE_CHANGED;

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
        Request changeOfferRequest =
                requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        requestService.updateRouteOfOffer(changeOfferRequest, token, callbackQuery.getMessage(), callbackQuery.getFrom());
        if (changeOfferRequest.getActivity() == Activity.DRIVER) {
            List<TransferItem> transferItems = requestService.findPassengersByRequestData(changeOfferRequest);
            return showSavedRequestWithoutDescriptionWithTransferItems(callbackQuery, changeOfferRequest, CANCEL_CHANGE_OFFER_REQUEST,
                    transferItems, DATES_WERE_CHANGED);

        } else {
            List<DriverItem> driverItems = requestService.findDriversByRequestData(changeOfferRequest);
            return showSavedRequestWithoutDescriptionWithDriverItems(callbackQuery, changeOfferRequest, CANCEL_CHANGE_OFFER_REQUEST,
                    driverItems, DATES_WERE_CHANGED);
        }
    }


}
