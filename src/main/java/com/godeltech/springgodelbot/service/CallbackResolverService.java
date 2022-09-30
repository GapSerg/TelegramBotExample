package com.godeltech.springgodelbot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

public interface CallbackResolverService {
    BotApiMethod handleCallBack(CallbackQuery callbackQuery);
}
