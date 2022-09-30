package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHECK_PASSENGER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SAVE_PASSENGER_WITHOUT_DESCRIPTION;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADD_DESCRIPTION_FOR_PASSENGER;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckPassengerCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return CHECK_PASSENGER_REQUEST.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got callback with type : {} by user : {}",
                CHECK_PASSENGER_REQUEST.name(),callbackQuery.getFrom().getUserName());
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        passengerRequest.setNeedForDescription(true);
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        requestService.clearDriverRequestsAndChangeOfferRequests(callbackQuery.getMessage().getChatId());
        return CallbackUtil.createEditMessageTextAfterConfirm(callbackQuery,SAVE_PASSENGER_WITHOUT_DESCRIPTION,
                WRITE_ADD_DESCRIPTION_FOR_PASSENGER);
    }
}
