package com.godeltech.springgodelbot.resolver.message.type;

import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface MessageType {

    String getMessageType();

    BotApiMethod createSendMessage(Message message);

}
