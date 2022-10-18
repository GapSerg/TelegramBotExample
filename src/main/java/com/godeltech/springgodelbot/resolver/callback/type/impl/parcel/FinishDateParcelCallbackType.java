package com.godeltech.springgodelbot.resolver.callback.type.impl.parcel;

import com.godeltech.springgodelbot.model.entity.DriverItem;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.CREATED_REQUEST;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishDateParcelCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return FINISH_CHOSE_DATE_PARCEL.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Callback : {} type with token : {} by user : {}",
                FINISH_CHOSE_DATE_PARCEL, token, callbackQuery.getFrom().getUserName());
        Request parcelRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());

        List<DriverItem> drivers = requestService.findDriversByRequestData(parcelRequest);

        String textMessage = getCompletedMessageAnswerWithDriverItems(drivers, parcelRequest, CREATED_REQUEST);
        return createSendMessageWithDoubleCheckOffer(callbackQuery, textMessage, CHECK_PARCEL_REQUEST.ordinal(),
                CANCEL_PARCEL_REQUEST.ordinal(), parcelRequest.getToken().getId());
    }
}
