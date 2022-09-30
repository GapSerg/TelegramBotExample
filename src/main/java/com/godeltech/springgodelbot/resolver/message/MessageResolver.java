package com.godeltech.springgodelbot.resolver.message;

import com.godeltech.springgodelbot.resolver.message.type.MessageType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class MessageResolver {

    private final Map<String, MessageType> messageTypeContext;

    @Autowired
    public MessageResolver(List<MessageType> messageTypes) {
        this.messageTypeContext = messageTypes.stream()
                .collect(Collectors.toMap(MessageType::getMessageType, Function.identity()));
    }

    public BotApiMethod getSendMessage(Messages messages, Message message) {
        return messageTypeContext.get(messages.name())
                .createSendMessage(message);
    }
}
