package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_SECOND_DATE_OF_OFFER;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.validSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.CallbackUtil.getListOfRequests;
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
        ChangeDriverRequest changeDriverRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage());
        return validSecondDate(changeDriverRequest.getFirstDate(), secondDate) ?
                updateDatesOfSupplierAndGetStartMenu(callbackQuery,
                        DATES_WERE_CHANGED, changeDriverRequest, secondDate) :
                createEditMessageForSecondDate(callbackQuery, changeDriverRequest.getFirstDate(),
                        INCORRECT_SECOND_DATE, CHANGE_SECOND_DATE_OF_OFFER.name(), secondDate);
    }

    private BotApiMethod updateDatesOfSupplierAndGetStartMenu(CallbackQuery callbackQuery, String text,
                                                              ChangeDriverRequest changeDriverRequest, LocalDate secondDate) {
        changeDriverRequest.setSecondDate(secondDate);
        requestService.updateDates(changeDriverRequest);
        String additionalText = changeDriverRequest.getActivity() == Activity.DRIVER ?
                getListOfRequests(requestService.findPassengersByRequestData(changeDriverRequest)) :
                getListOfRequests(requestService.findDriversByRequestData(changeDriverRequest));
        return getStartMenu(callbackQuery.getMessage(), text + additionalText);
    }
}
