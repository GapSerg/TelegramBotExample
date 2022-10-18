package com.godeltech.springgodelbot.resolver.callback.type.impl.parcel;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_PARCEL_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHECK_PARCEL_REQUEST;
import static com.godeltech.springgodelbot.util.CallbackUtil.createEditMessageTextAfterConfirmForParcel;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADD_DESCRIPTION_FOR_PARCEL;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckParcelCallbackType implements CallbackType {
    private final RequestService requestService;
    private final UserService userService;


    @Override
    public Integer getCallbackName() {
        return CHECK_PARCEL_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback with type : {} with token : {} by user : {}",
                CHECK_PARCEL_REQUEST.name(), token , callbackQuery.getFrom().getUserName());
        Request parcelRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        userService.userAuthorization(callbackQuery.getFrom(),callbackQuery.getMessage(),false);
        parcelRequest = requestService.prepareRequestForDescription(parcelRequest);
        return createEditMessageTextAfterConfirmForParcel(callbackQuery, CANCEL_PARCEL_REQUEST.ordinal(),
                WRITE_ADD_DESCRIPTION_FOR_PARCEL, parcelRequest.getToken().getId());
    }
}
