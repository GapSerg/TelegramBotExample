package com.godeltech.springgodelbot.exception;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.godeltech.springgodelbot.util.ConstantUtil.AUTHORIZATION_PATTERN;

@Getter
public class UserAuthorizationException extends RuntimeException {
    private Message botMessage;
    private boolean isOnText;
    public UserAuthorizationException(Class<?> resourceType, String fieldName, Object fieldValue, Message botMessage, boolean isOnText) {
        super(String.format(AUTHORIZATION_PATTERN, resourceType, fieldName, fieldValue));
        this.botMessage = botMessage;
        this.isOnText=isOnText;
    }
}
