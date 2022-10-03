package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHECK_PASSENGER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SAVE_PASSENGER_WITHOUT_DESCRIPTION;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADD_DESCRIPTION_FOR_PASSENGER;

@Component
@Slf4j
public class CheckPassengerCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public CheckPassengerCallbackType(RequestService requestService,
                                      @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }


    @Override
    public String getCallbackName() {
        return CHECK_PASSENGER_REQUEST.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got callback with type : {} by user : {}",
                CHECK_PASSENGER_REQUEST.name(), callbackQuery.getFrom().getUserName());
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        checkUsername(callbackQuery, passengerRequest);
        passengerRequest.setNeedForDescription(true);
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        requestService.clearDriverRequestsAndChangeOfferRequests(callbackQuery.getMessage().getChatId());
        return CallbackUtil.createEditMessageTextAfterConfirm(callbackQuery, SAVE_PASSENGER_WITHOUT_DESCRIPTION,
                WRITE_ADD_DESCRIPTION_FOR_PASSENGER);
    }

    private void checkUsername(CallbackQuery callbackQuery, PassengerRequest passengerRequest) {
        if (callbackQuery.getFrom().getUserName() == null){
            tudaSudaTelegramBot.deleteMessages(passengerRequest.getChatId(), passengerRequest.getMessages());
            throw new UserAuthorizationException(UserDto.class, "username", null, callbackQuery.getMessage(),false );}
    }
}
