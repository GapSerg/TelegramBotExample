package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.MAIN_MENU;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class MainMenuActivityCallbackType implements CallbackType {

    private final UserService userService;
    private final UserMapper userMapper;
    private final TokenService tokenService;

    @Override
    public Integer getCallbackName() {
        return MAIN_MENU.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got {} callback type", MAIN_MENU);
        String[] data = callbackQuery.getData().split(SPLITTER);
        if (callbackQuery.getFrom().getUserName() == null)
            return makeEditMessageForUserWithoutUsername(callbackQuery.getMessage());
        if (!userService.existsByIdAndUsername(callbackQuery.getFrom().getId(), callbackQuery.getFrom().getUserName()))
            userService.save(userMapper.mapToUserEntity(callbackQuery.getFrom()), callbackQuery.getMessage());
        if (data.length>1){
            tokenService.deleteToken(getCallbackToken(callbackQuery.getData()));
        }
        return getStartMenu(callbackQuery,tokenService.createToken());
    }

}
