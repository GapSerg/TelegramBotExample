package com.godeltech.springgodelbot.resolver.callback.type.impl.parcel;

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
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.getCurrentSuitableActivities;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.getDatesInf;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE_DRIVER;

@Component
@RequiredArgsConstructor
@Slf4j
public class FirstDateParcelCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return FIRST_DATE_PARCEL.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate firstDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} type with first date :{} with token: {} by user : {}",
                FIRST_DATE_PARCEL, firstDate, token,callbackQuery.getFrom().getUserName());
        Request parcelRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        parcelRequest.setFirstDate(firstDate);
        parcelRequest= requestService.updateRequest(parcelRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        String textMessage = String.format(CHOSEN_FIRST_DATE, parcelRequest.getActivity().getTextMessage(),
                getCurrentRoute(parcelRequest.getCities()),
                getDatesInf(parcelRequest.getFirstDate()));
        return createEditMessageForSecondDate(callbackQuery, parcelRequest.getFirstDate(),
                textMessage, SECOND_DATE_PARCEL.ordinal(), CANCEL_PARCEL_REQUEST.ordinal(), token);
    }
}
