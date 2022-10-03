package com.godeltech.springgodelbot.handler;

import com.godeltech.springgodelbot.exception.RequestNotFoundException;
import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;

@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class BotHandler {

    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    @ExceptionHandler(value = RequestNotFoundException.class)
    @SneakyThrows
    public void handleRequestNotFoundException(RequestNotFoundException exception) {
        log.error(exception.getMessage());
        tudaSudaTelegramBot.execute(getStartMenu(exception.getBotMessage(),
                "Something was wrong, Please make try one more time"));
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
        tudaSudaTelegramBot.execute(getStartMenu(exception.getChatId(), "There is no such type of request, please try again"));
    }

}
