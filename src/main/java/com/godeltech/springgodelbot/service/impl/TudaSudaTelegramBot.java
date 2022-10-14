package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.MembershipException;
import com.godeltech.springgodelbot.exception.MessageFromGroupException;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.service.CallbackResolverService;
import com.godeltech.springgodelbot.service.MessageResolverService;
import com.godeltech.springgodelbot.service.TokenService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChatMember;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMember;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberBanned;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberLeft;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Getter
@Setter
public class TudaSudaTelegramBot extends SpringWebhookBot {

    private final MessageResolverService messageResolverService;
    private final CallbackResolverService callbackResolverService;
    private final TokenService tokenService;
    private String botUsername;
    private String botToken;
    private String botPath;
    private String chmokiId;


    public TudaSudaTelegramBot(SetWebhook setWebhookInstance, MessageResolverService messageResolverService, CallbackResolverService callbackResolverService, TokenService tokenService) {
        super(setWebhookInstance);
        this.messageResolverService = messageResolverService;
        this.callbackResolverService = callbackResolverService;
        this.tokenService = tokenService;
    }

    public TudaSudaTelegramBot(SetWebhook setWebhookInstance, DefaultBotOptions defaultBotOptions, MessageResolverService messageResolverService, CallbackResolverService callbackResolverService, TokenService tokenService) {
        super(defaultBotOptions, setWebhookInstance);
        this.messageResolverService = messageResolverService;
        this.callbackResolverService = callbackResolverService;
        this.tokenService = tokenService;
    }

    @Override
    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        BotApiMethod botApiMethod = null;
        if (update.hasMessage()) {
            checkMembership(update.getMessage());
            botApiMethod = messageResolverService.handleMessage(update.getMessage());
            return botApiMethod;
        } else if (update.hasCallbackQuery()) {
            checkMembership(update.getCallbackQuery().getFrom(),update.getCallbackQuery().getMessage());
            botApiMethod = callbackResolverService.handleCallBack(update.getCallbackQuery());
            return botApiMethod;
        }
        return botApiMethod;
    }

    public void checkMembership(Message message) {
        try {
            if (message.getChat().getId().toString().equals(chmokiId)){
                throw new MessageFromGroupException();
            }
            log.info("Is he member of group? user : {}", message.getFrom().getUserName());
            ChatMember chatMember = execute(GetChatMember.builder()
                    .chatId(chmokiId)
                    .userId(message.getFrom().getId())
                    .build());
            if (chatMember instanceof ChatMemberBanned || chatMember instanceof ChatMemberLeft)
                throw new MembershipException(message.getFrom().getId(), message.getFrom().getUserName(), message, true);
        } catch (TelegramApiException e) {
            throw new MembershipException(message.getFrom().getId(), message.getFrom().getUserName(), message, true);
        }
    }

    public void checkMembership(User user, Message message) {
        try {
            log.info("Is he member of group? user : {}",user.getUserName());
            Chat chat = message.getChat();
            ChatMember chatMember = execute(GetChatMember.builder()
                    .chatId(chmokiId)
                    .userId(user.getId())
                    .build());
            if (chatMember instanceof ChatMemberBanned || chatMember instanceof ChatMemberLeft)
                throw new MembershipException(user.getId(), user.getUserName(), message, false);
        } catch (TelegramApiException e) {
            throw new MembershipException(user.getId(), user.getUserName(), message, false);
        }
    }

    public void editPreviousMessage(CallbackQuery callbackQuery, String answer) {
        try {
            log.info("Edit previous message with message Id : {}", callbackQuery.getMessage().getMessageId());
            execute(EditMessageText.builder()
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text(answer)
                    .build());
        } catch (TelegramApiException e) {
            log.error("There is a tg exception: {}", e.getMessage());
            throw new UnknownCommandException(callbackQuery.getMessage());
        }
    }

    public void deleteMessages(Long chatId, Set<Integer> messages) {
        log.info("Delete messages with chat id : {} and messages : {}", chatId, messages);
        messages.forEach(message -> {
                    deleteMessage(chatId, message);
                }
        );
    }

    public void deleteMessage(Long chatId, Integer message) {
        try {
            execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(message)
                    .build());
        } catch (TelegramApiException e) {
            log.error("Can't delete message with message Id : {}",message);
        }
    }

    public void deleteExpiredTokens(LocalDateTime date) {
        tokenService.deleteNonUsableExpiredTokens(date);
        List<Token> tokens = tokenService.getUsableExpiredTokens(date);
        tokens.forEach(token -> deleteMessage(token.getChatId(), token.getMessageId()));
        tokenService.deleteAll(tokens);
    }
}