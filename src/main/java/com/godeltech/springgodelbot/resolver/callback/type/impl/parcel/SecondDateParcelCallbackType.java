package com.godeltech.springgodelbot.resolver.callback.type.impl.parcel;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.getCurrentSuitableActivities;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_SECOND_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_SECOND_DATE_DRIVER;

@Component
@Slf4j
public class SecondDateParcelCallbackType implements CallbackType {
    private final RequestService requestService;

    public SecondDateParcelCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return SECOND_DATE_PARCEL.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        LocalDate chosenDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with second date :{} and token : {} by user : {}",
                SECOND_DATE_PARCEL, chosenDate, token, callbackQuery.getFrom().getUserName());
        Request parcelRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        setDatesToRequest(chosenDate, parcelRequest);
        parcelRequest= requestService.updateRequest(parcelRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        String textMessage = String.format(CHOSEN_SECOND_DATE, parcelRequest.getActivity().getTextMessage(),
                getCurrentRoute(parcelRequest.getCities()), getDatesInf(parcelRequest.getFirstDate(),
                        parcelRequest.getSecondDate()));
        return createEditMessageForSecondDate(callbackQuery, parcelRequest.getFirstDate(), textMessage,
                SECOND_DATE_PARCEL.ordinal(), CANCEL_PARCEL_REQUEST.ordinal(), parcelRequest.getSecondDate(), token);
    }


}
