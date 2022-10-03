package com.godeltech.springgodelbot.exception;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.godeltech.springgodelbot.util.ConstantUtil.UNKNOWN_COMMAND;

@Getter
public class UnknownCommandException extends RuntimeException {
    private Message telegramMessage;

    public UnknownCommandException() {
        super(UNKNOWN_COMMAND);
    }

    public UnknownCommandException(Message message) {
        super(UNKNOWN_COMMAND);
        this.telegramMessage = message;
    }
}
