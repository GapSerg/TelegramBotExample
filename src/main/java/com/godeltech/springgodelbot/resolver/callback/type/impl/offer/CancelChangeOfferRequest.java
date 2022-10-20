package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.START_FROM_THE_BEGINNING;

@Component
@Slf4j
public class CancelChangeOfferRequest implements CallbackType {

    private final RequestService requestService;
    private final TokenService tokenService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public CancelChangeOfferRequest(RequestService requestService, TokenService tokenService,
                                    @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tokenService = tokenService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public Integer getCallbackName() {
        return CANCEL_CHANGE_OFFER_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback : {} with token : {} by user : {}",
                CANCEL_CHANGE_OFFER_REQUEST,token,callbackQuery.getFrom().getUserName());
        Request changeOfferRequest = requestService.getRequest(callbackQuery.getMessage(),token,callbackQuery.getFrom() );
        if (changeOfferRequest.getToken().getMessageId()==null)
            changeOfferRequest.getToken().setMessageId(callbackQuery.getMessage().getMessageId());
        requestService.deleteRequest(changeOfferRequest,callbackQuery.getMessage());
        tudaSudaTelegramBot.deleteMessage(callbackQuery.getMessage().getChatId(), callbackQuery.getMessage().getMessageId());
        Token createdToken = tokenService.createToken(callbackQuery.getFrom().getId(),
                callbackQuery.getMessage().getMessageId(), callbackQuery.getMessage().getChatId());
        return getStartMenu(callbackQuery.getMessage().getChatId(), START_FROM_THE_BEGINNING,createdToken.getId());
    }
}
