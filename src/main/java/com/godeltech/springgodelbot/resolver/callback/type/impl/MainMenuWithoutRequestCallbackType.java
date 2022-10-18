package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.MAIN_MENU_WITHOUT_REQUEST;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;

@Component
@Slf4j
public class MainMenuWithoutRequestCallbackType implements CallbackType {
    private final TokenService tokenService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public MainMenuWithoutRequestCallbackType(TokenService tokenService,
                                              @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.tokenService = tokenService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public Integer getCallbackName() {
        return MAIN_MENU_WITHOUT_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String tokenId = getCallbackToken(callbackQuery.getData());
        log.info("Get {} callback type with tokenId : {} by user : {}",
                MAIN_MENU_WITHOUT_REQUEST, tokenId, callbackQuery.getFrom().getUserName());
        Token token = tokenService.getById(tokenId, callbackQuery.getMessage(), callbackQuery.getFrom());
        tudaSudaTelegramBot.deleteMessage(token.getChatId(), token.getMessageId());
        tokenService.deleteToken(tokenId, callbackQuery.getMessage(), callbackQuery.getFrom());
        Token createdToken = tokenService.createToken(callbackQuery.getFrom().getId(), callbackQuery.getMessage().getChatId());
        return getStartMenu(callbackQuery.getMessage().getChatId(), createdToken.getId());
    }
}
