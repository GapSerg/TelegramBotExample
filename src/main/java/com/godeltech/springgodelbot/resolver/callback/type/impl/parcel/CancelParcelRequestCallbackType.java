package com.godeltech.springgodelbot.resolver.callback.type.impl.parcel;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.godeltech.springgodelbot.util.ConstantUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@Slf4j
public class CancelParcelRequestCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final TokenService tokenService;

    public CancelParcelRequestCallbackType(RequestService requestService,
                                           @Lazy TudaSudaTelegramBot tudaSudaTelegramBot, TokenService tokenService) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.tokenService = tokenService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.CANCEL_PARCEL_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback : {} with token : {} by user : {}",
                Callbacks.CANCEL_PARCEL_REQUEST, token, callbackQuery.getFrom().getUserName());
        Request parcelRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        if (parcelRequest.getToken().getMessageId() == null)
            parcelRequest.getToken().setMessageId(callbackQuery.getMessage().getMessageId());
        requestService.deleteRequest(parcelRequest, callbackQuery.getMessage());
        tudaSudaTelegramBot.deleteMessage(parcelRequest.getToken().getChatId(), parcelRequest.getToken().getMessageId());
        Token createdToken = tokenService.createToken(callbackQuery.getFrom().getId(),
                callbackQuery.getMessage().getMessageId(), callbackQuery.getMessage().getChatId());
        return getStartMenu(callbackQuery.getMessage().getChatId(), START_FROM_THE_BEGINNING, createdToken.getId());
    }
}
