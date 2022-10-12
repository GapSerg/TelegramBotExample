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
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageTextForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE_OF_OFFER;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDateOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CANCEL_DATE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with canceled date :{} and token: {}", CANCEL_DATE_OF_OFFER, canceledDate, token);
        Request changeOfferRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());

        return canceledDate.equals(changeOfferRequest.getFirstDate()) ?
                getEditMessageWithCanceledFirstDate(callbackQuery, changeOfferRequest, canceledDate) :
                getEditMessageWithCanceledSecondDate(callbackQuery, changeOfferRequest);
    }


    private BotApiMethod getEditMessageWithCanceledSecondDate(CallbackQuery callbackQuery, Request changeOfferRequest) {
        changeOfferRequest.setSecondDate(null);
        changeOfferRequest = requestService.updateRequest(changeOfferRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = String.format(CHOSEN_FIRST_DATE_OF_OFFER, changeOfferRequest.getFirstDate());
        return createEditMessageForSecondDate(callbackQuery, changeOfferRequest.getFirstDate(),
                textMessage, CHANGE_SECOND_DATE_OF_OFFER.ordinal(), RETURN_TO_CHANGE_OF_OFFER.ordinal(),
                changeOfferRequest.getToken().getId());
    }

    private BotApiMethod getEditMessageWithCanceledFirstDate(CallbackQuery callbackQuery, Request changeOfferRequest,
                                                             LocalDate canceledDate) {
        if (changeOfferRequest.getSecondDate() != null) {
            changeOfferRequest.setFirstDate(changeOfferRequest.getSecondDate());
            changeOfferRequest.setSecondDate(null);
            changeOfferRequest = requestService.updateRequest(changeOfferRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
            String textMessage = String.format(CHOSEN_FIRST_DATE_OF_OFFER, changeOfferRequest.getFirstDate());
            return createEditMessageForSecondDate(callbackQuery, changeOfferRequest.getFirstDate(),
                    textMessage, CHANGE_SECOND_DATE_OF_OFFER.ordinal(), RETURN_TO_CHANGE_OF_OFFER.ordinal(),
                    changeOfferRequest.getToken().getId());
        }
        changeOfferRequest.setFirstDate(null);
        String textMessage = "Chose the date";
        changeOfferRequest = requestService.updateRequest(changeOfferRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
        return createEditMessageTextForFirstDate(callbackQuery, CHANGE_FIRST_DATE_OF_OFFER.ordinal(),
                RETURN_TO_CHANGE_OF_OFFER.ordinal(), textMessage, canceledDate, changeOfferRequest.getToken().getId());
    }
}
