package com.godeltech.springgodelbot.resolver.message.type.impl;

import com.godeltech.springgodelbot.exception.MessageFromGroupException;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.resolver.message.Messages;
import com.godeltech.springgodelbot.resolver.message.type.MessageType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.UserService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.godeltech.springgodelbot.util.BotMenu;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChat;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeChatMember;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;

import java.util.Optional;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.ConstantUtil.START_MESSAGE;

@Component
@Slf4j
public class TextAndEntityMessageType implements MessageType {
    private final UserService userService;
    private final TokenService tokenService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public TextAndEntityMessageType(UserService userService,
                                    TokenService tokenService,
                                    @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.userService = userService;
        this.tokenService = tokenService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public String getMessageType() {
        return Messages.TEXT_AND_ENTITY.name();
    }

    @Override
    public BotApiMethod createSendMessage(Message message) {
        return getSendMessage(message);
    }


    private BotApiMethod getSendMessage(Message message) {
        Optional<MessageEntity> commandEntity = message.getEntities().stream()
                .filter(entity -> "bot_command".equals(entity.getType()))
                .findFirst();
        if (commandEntity.isPresent()) {
            String command = message.getText().substring(commandEntity.get().getOffset(), commandEntity.get().getLength());
            switch (command) {
                case "/start":
                    log.info("Got /start message");
                    return makeSendMessageForUser(message);
                case "/help":
                    log.info("Got /help message");
                    return makeHelpSendMessage(message);
                default:
                    log.error("Got /help unknown entity");
                    throw new UnknownCommandException(message);
            }
        } else {
            throw new UnknownCommandException(message);
        }
    }

    private SendMessage makeHelpSendMessage(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("This bot was created for people who need help moving to some place or for those who can offer this help")
                .build();
    }

    @SneakyThrows
    private BotApiMethod makeSendMessageForUser(Message message) {
        tudaSudaTelegramBot.checkMembership(message);
        userService.userAuthorization(message.getFrom(),message,true);
        tudaSudaTelegramBot.execute(new SetMyCommands(BotMenu.getCommands(),
                new BotCommandScopeChat(message.getChat().getId().toString()),null));
        Token createdToken = tokenService.createToken(message.getFrom().getId(), message.getChatId());
        return getStartMenu(message.getChatId(), START_MESSAGE, createdToken.getId());
    }
}
