package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.service.CallbackResolverService;
import com.godeltech.springgodelbot.resolver.callback.CallbackResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

@Service
@Slf4j
@RequiredArgsConstructor
public class CallbackResolverServiceImpl implements CallbackResolverService {

    private final CallbackResolver callbackResolver;

    @Override
    public BotApiMethod handleCallBack(CallbackQuery callbackQuery) {
        return callbackResolver.getSendMessage(callbackQuery);
    }
}
