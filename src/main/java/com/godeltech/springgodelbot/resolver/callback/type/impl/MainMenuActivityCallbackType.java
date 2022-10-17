package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.UserService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.MAIN_MENU;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.SPLITTER;

@Component
@Slf4j
public class MainMenuActivityCallbackType implements CallbackType {

    private final UserService userService;
    private final TokenService tokenService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final RequestService requestService;

    public MainMenuActivityCallbackType(UserService userService,
                                        TokenService tokenService,
                                        @Lazy TudaSudaTelegramBot tudaSudaTelegramBot, RequestService requestService) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return MAIN_MENU.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got {} callback type by user : {}", MAIN_MENU, callbackQuery.getFrom().getUserName());
        String[] data = callbackQuery.getData().split(SPLITTER);
        tudaSudaTelegramBot.checkMembership(callbackQuery.getFrom(), callbackQuery.getMessage());
        userService.userAuthorization(callbackQuery.getFrom(), callbackQuery.getMessage(), false);
        if (data.length > 1) {
            Request request =
                    requestService.getRequest(callbackQuery.getMessage(), getCallbackToken(callbackQuery.getData()),
                            callbackQuery.getFrom());
            requestService.deleteRequest(request, callbackQuery.getMessage());
        }
        Token token = tokenService.createToken(callbackQuery.getFrom().getId(),
                callbackQuery.getMessage().getMessageId(),
                callbackQuery.getMessage().getChatId());
        return getStartMenu(callbackQuery, token.getId());
    }

}
