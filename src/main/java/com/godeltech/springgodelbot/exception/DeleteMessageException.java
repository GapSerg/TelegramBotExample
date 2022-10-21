package com.godeltech.springgodelbot.exception;

import lombok.Data;

@Data
public class DeleteMessageException extends RuntimeException {
    private final Long chatId;
    private final Integer messageId;

    public DeleteMessageException(Long chatId, Integer message) {
        super("Can't delete message");
        this.chatId = chatId;
        this.messageId = message;
    }
}
