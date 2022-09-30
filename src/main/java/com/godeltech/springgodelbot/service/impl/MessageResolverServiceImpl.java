package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.service.MessageResolverService;
import com.godeltech.springgodelbot.resolver.message.MessageResolver;
import com.godeltech.springgodelbot.resolver.message.Messages;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Message;

@Service
@RequiredArgsConstructor
public class MessageResolverServiceImpl implements MessageResolverService {

    private final MessageResolver messageResolver;

    @Override
    public BotApiMethod handleMessage(Message message) {
        if (message.hasText() && message.hasEntities()) {
            return messageResolver.getSendMessage(Messages.TEXT_AND_ENTITY, message);
        } else {
            return messageResolver.getSendMessage(Messages.ONLY_TEXT, message);
        }
    }

}
