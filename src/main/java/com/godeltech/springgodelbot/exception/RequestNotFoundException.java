package com.godeltech.springgodelbot.exception;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.godeltech.springgodelbot.util.ConstantUtil.NO_FOUND_PATTERN;

@Getter
public class RequestNotFoundException extends RuntimeException {
    private Message botMessage;

    public RequestNotFoundException(Class<?> resourceType, String fieldName, Object fieldValue, Message botMessage) {
        super(String.format(NO_FOUND_PATTERN, resourceType, fieldName, fieldValue));
        this.botMessage = botMessage;
    }

}
