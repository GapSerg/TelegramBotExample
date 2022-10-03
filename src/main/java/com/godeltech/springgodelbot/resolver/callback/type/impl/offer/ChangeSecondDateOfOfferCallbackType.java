package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
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
import static com.godeltech.springgodelbot.util.CallbackUtil.getAvailableOffersList;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.DATES_WERE_CHANGED;
import static com.godeltech.springgodelbot.util.ConstantUtil.INCORRECT_SECOND_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeSecondDateOfOfferCallbackType implements CallbackType {


    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return CHANGE_SECOND_DATE_OF_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        LocalDate secondDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Change date of offer with date :{} by user:{}", secondDate, callbackQuery.getFrom().getUserName());
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage());
        return validSecondDate(changeOfferRequest.getFirstDate(), secondDate) ?
                updateDatesOfSupplierAndGetStartMenu(callbackQuery,
                        DATES_WERE_CHANGED, changeOfferRequest, secondDate) :
                createEditMessageForSecondDate(callbackQuery, changeOfferRequest.getFirstDate(),
                        INCORRECT_SECOND_DATE, CHANGE_SECOND_DATE_OF_OFFER.name(), secondDate);
    }

    private BotApiMethod updateDatesOfSupplierAndGetStartMenu(CallbackQuery callbackQuery, String text,
                                                              ChangeOfferRequest changeOfferRequest, LocalDate secondDate) {
        changeOfferRequest.setSecondDate(secondDate);
        requestService.updateDates(changeOfferRequest);
        List<? extends Request> requests = changeOfferRequest.getActivity() == Activity.DRIVER ?
                requestService.findPassengersByRequestData(changeOfferRequest) :
                requestService.findDriversByRequestData(changeOfferRequest);
        return getAvailableOffersList(requests,callbackQuery, text);
    }
}
