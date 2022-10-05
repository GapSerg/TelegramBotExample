package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.MessageService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;
import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_SECOND_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.validSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.DATES_WERE_CHANGED;
import static com.godeltech.springgodelbot.util.ConstantUtil.INCORRECT_SECOND_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeSecondDateOfOfferCallbackType implements CallbackType {


    private final RequestService requestService;
    private final MessageService messageService;

    @Override
    public Integer getCallbackName() {
        return CHANGE_SECOND_DATE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate secondDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Change date of offer with date :{} with token: {}", secondDate, token);
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(),token );
        return validSecondDate(changeOfferRequest.getFirstDate(), secondDate) ?
                updateDatesOfSupplierAndGetStartMenu(callbackQuery,
                        DATES_WERE_CHANGED, changeOfferRequest, secondDate,token) :
                createEditMessageForSecondDate(callbackQuery, changeOfferRequest.getFirstDate(),
                        INCORRECT_SECOND_DATE, CHANGE_SECOND_DATE_OF_OFFER.ordinal(), secondDate,token );
    }

    private BotApiMethod updateDatesOfSupplierAndGetStartMenu(CallbackQuery callbackQuery, String text,
                                                              ChangeOfferRequest changeOfferRequest, LocalDate secondDate, String token) {
        changeOfferRequest.setSecondDate(secondDate);
        requestService.updateDates(changeOfferRequest,token );
        List<? extends Request> requests = changeOfferRequest.getActivity() == Activity.DRIVER ?
                requestService.findPassengersByRequestData(changeOfferRequest) :
                requestService.findDriversByRequestData(changeOfferRequest);
        messageService.deleteToken(token);
        return getAvailableOffersList(requests,callbackQuery, text,token);
    }
}
