package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

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
import static com.godeltech.springgodelbot.util.ConstantUtil.START_FROM_THE_BEGINNING;

@Component
@Slf4j
public class CancelDriverRequestCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final TokenService tokenService;

    public CancelDriverRequestCallbackType(RequestService requestService,
                                           @Lazy TudaSudaTelegramBot tudaSudaTelegramBot,
                                           TokenService tokenService) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.tokenService = tokenService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.CANCEL_DRIVER_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback : {} with token : {} by user : {}",
                Callbacks.CANCEL_DRIVER_REQUEST, token, callbackQuery.getFrom().getUserName());
        Request driverRequest =
                requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        if (driverRequest.getToken().getMessageId()==null)
            driverRequest.getToken().setMessageId(callbackQuery.getMessage().getMessageId());
        requestService.deleteRequest(driverRequest, callbackQuery.getMessage());
        tudaSudaTelegramBot.deleteMessage(driverRequest.getToken().getChatId(), driverRequest.getToken().getMessageId());
        Token createdToken = tokenService.createToken(callbackQuery.getFrom().getId(),
                callbackQuery.getMessage().getMessageId(), callbackQuery.getMessage().getChatId());
        return getStartMenu(callbackQuery.getMessage().getChatId(), START_FROM_THE_BEGINNING, createdToken.getId());
    }
}
