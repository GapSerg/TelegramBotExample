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
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageForSecondDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createEditMessageTextForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_FIRST_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_FIRST_DATE;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDateParcelCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CANCEL_DATE_PARCEL.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} type with token : {} by user : {}",
                CANCEL_DATE_PARCEL, token, callbackQuery.getFrom().getUserName());
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        Request parcelRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        return canceledDate.equals(parcelRequest.getFirstDate()) ?
                getEditMessageWithCanceledFirstDate(callbackQuery, parcelRequest, canceledDate) :
                getEditMessageWithCanceledSecondDate(callbackQuery, parcelRequest);
    }


    private BotApiMethod getEditMessageWithCanceledSecondDate(CallbackQuery callbackQuery, Request parcelRequest) {
        parcelRequest.setSecondDate(null);
        parcelRequest = requestService.updateRequest(parcelRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = String.format(CHOSEN_FIRST_DATE, parcelRequest.getActivity(), getCurrentRoute(parcelRequest.getCities()),
                parcelRequest.getFirstDate());
        return createEditMessageForSecondDate(callbackQuery, parcelRequest.getFirstDate(),
                textMessage, SECOND_DATE_PARCEL.ordinal(), CANCEL_DATE_PARCEL.ordinal(), parcelRequest.getToken().getId());
    }

    private BotApiMethod getEditMessageWithCanceledFirstDate(CallbackQuery callbackQuery, Request parcelRequest,
                                                             LocalDate canceledDate) {
        if (parcelRequest.getSecondDate() != null) {
            parcelRequest.setFirstDate(parcelRequest.getSecondDate());
            parcelRequest.setSecondDate(null);
            parcelRequest = requestService.updateRequest(parcelRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
            String textMessage = String.format(CHOSEN_FIRST_DATE, parcelRequest.getActivity(), getCurrentRoute(parcelRequest.getCities()),
                    parcelRequest.getFirstDate());
            return createEditMessageForSecondDate(callbackQuery, parcelRequest.getFirstDate(),
                    textMessage, SECOND_DATE_PARCEL.ordinal(), CANCEL_DATE_PARCEL.ordinal(), parcelRequest.getToken().getId());
        }
        parcelRequest.setFirstDate(null);
        parcelRequest = requestService.updateRequest(parcelRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        String textMessage = String.format(CHOOSE_THE_FIRST_DATE, parcelRequest.getActivity(), getCurrentRoute(parcelRequest.getCities()));
        return createEditMessageTextForFirstDate(callbackQuery, FIRST_DATE_PARCEL.ordinal(),
                CANCEL_PARCEL_REQUEST.ordinal(), textMessage, canceledDate, parcelRequest.getToken().getId());
    }
}
