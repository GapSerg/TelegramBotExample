package com.godeltech.springgodelbot.handler;

import com.godeltech.springgodelbot.exception.*;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class BotHandler {

    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final TokenService tokenService;

    @ExceptionHandler(value = RequestNotFoundException.class)
    @SneakyThrows
    public void handleRequestNotFoundException(RequestNotFoundException exception) {
        log.error(exception.getMessage());
        Token createdToken = tokenService.createToken(exception.getBotMessage().getFrom().getId(),
                exception.getBotMessage().getMessageId(), exception.getBotMessage().getChatId());
        tudaSudaTelegramBot.execute(getStartMenu(exception.getBotMessage(),
                "Something was wrong, Please make try one more time", createdToken.getId()));
    }

    @ExceptionHandler(value = UserAuthorizationException.class)
    @SneakyThrows
    public void handleUserAuthorizationException(UserAuthorizationException exception) {
        log.error(exception.getMessage());
        if (exception.isOnText()) {
            tudaSudaTelegramBot.execute(CallbackUtil.makeSendMessageForUserWithoutUsername(exception.getBotMessage()));
        } else {
            tudaSudaTelegramBot.execute(CallbackUtil.makeEditMessageForUserWithoutUsername(exception.getBotMessage()));
        }
    }

    @ExceptionHandler(value = UnknownCommandException.class)
    @SneakyThrows
    public void handleUnknownCommandException(UnknownCommandException exception) {
        log.error(exception.getMessage());
//        tudaSudaTelegramBot.execute(makeSendMessageForUser(exception.getTelegramMessage()));
    }

    @ExceptionHandler(value = ResourceNotFoundException.class)
    @SneakyThrows
    public void handleResourceNotFoundException(ResourceNotFoundException exception) {
        log.error(exception.getMessage());
        Token createdToken = tokenService.createToken(exception.getBotMessage().getFrom().getId(), exception.getBotMessage().getMessageId(), exception.getBotMessage().getChatId());
        tudaSudaTelegramBot.deleteMessage(exception.getBotMessage().getChatId(), exception.getBotMessage().getMessageId());
        tudaSudaTelegramBot.execute(getStartMenu(exception.getBotMessage().getChatId(),
                "There is no such type of request, please try again", createdToken.getId()));
    }

    @ExceptionHandler(value = RepeatedTokenMessageException.class)
    public void handleRepeatedTokenMessageException(RepeatedTokenMessageException exception) {
        log.error(exception.getMessage());
    }

    @ExceptionHandler(value = RuntimeException.class)
    public void handleRuntimeException(RuntimeException exception) {
        log.error(exception.getMessage());
    }

    @ExceptionHandler(value = ResourceNotUniqueException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public void handleResourceNotUniqueException(ResourceNotUniqueException resourceNotUniqueException) {
        log.error(resourceNotUniqueException.getMessage());
    }

    @ExceptionHandler(value = MembershipException.class)
    @SneakyThrows
    public void handleMembershipException(MembershipException membershipException) {
        log.error(membershipException.getMessage());
        Message message = membershipException.getBotMessage();
        if (membershipException.isFromMessage()) {
            tudaSudaTelegramBot.execute(createSendMessage(message));
        }
    }

    private SendMessage createSendMessage(Message message) {
        return SendMessage.builder()
                .chatId(message.getChatId().toString())
                .text("You aren't a member of chmoki group. Please correct it")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(
                                InlineKeyboardButton.builder()
                                        .text("I've corrected it")
                                        .callbackData(String.valueOf(Callbacks.MAIN_MENU.ordinal()))
                                        .build()
                        )))
                        .build())

                .build();
    }

    private EditMessageText createEditMessageText(Message message) {
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text("You aren't a member of chmoki group. Please correct it")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(
                                InlineKeyboardButton.builder()
                                        .text("I've corrected it")
                                        .callbackData(String.valueOf(Callbacks.MAIN_MENU.ordinal()))
                                        .build()
                        )))
                        .build())

                .build();
    }
}
