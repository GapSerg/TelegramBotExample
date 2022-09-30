package com.godeltech.springgodelbot.service;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageResolverService {

    BotApiMethod handleMessage(Message message);
}
