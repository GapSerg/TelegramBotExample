package com.godeltech.springgodelbot.resolver.callback;

import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class CallbackResolver {

    private final Map<String, CallbackType> callbackTypeContext;

    @Autowired
    public CallbackResolver(List<CallbackType> callbackTypes) {
        this.callbackTypeContext = callbackTypes.stream()
                .collect(Collectors.toMap(CallbackType::getCallbackName, Function.identity()));
    }

    public BotApiMethod getSendMessage(CallbackQuery callbackQuery) {
        return callbackTypeContext.get(callbackQuery.getData().split("&")[0])
                .createSendMessage(callbackQuery);
    }
}
