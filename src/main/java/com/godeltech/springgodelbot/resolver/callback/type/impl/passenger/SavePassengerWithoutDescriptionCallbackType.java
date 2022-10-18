package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.model.entity.DriverItem;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.SUCCESSFUL_REQUEST_SAVING;

@Component
@Slf4j
public class SavePassengerWithoutDescriptionCallbackType implements CallbackType {

    private final RequestService requestService;


    public SavePassengerWithoutDescriptionCallbackType(RequestService requestService) {
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return SAVE_PASSENGER_WITHOUT_DESCRIPTION.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback with type : {} with token : {} by user : {}",
                SAVE_PASSENGER_WITHOUT_DESCRIPTION, token, callbackQuery.getFrom().getUserName());
        Request passengerRequest =requestService.getRequest(callbackQuery.getMessage(),token,callbackQuery.getFrom() );
        requestService.saveTransferItem(passengerRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        List<DriverItem> driverItems = requestService.findDriversByRequestData(passengerRequest);
        return showSavedRequestWithoutDescriptionWithDriverItems(callbackQuery, passengerRequest,CANCEL_PASSENGER_REQUEST,
                driverItems,SUCCESSFUL_REQUEST_SAVING);
    }
}
