package com.godeltech.springgodelbot.resolver.callback.type;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackType {

    String getCallbackName();

    BotApiMethod createSendMessage(CallbackQuery callbackQuery);
}
