package com.godeltech.springgodelbot.exception;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.godeltech.springgodelbot.util.ConstantUtil.NO_FOUND_PATTERN;
import static com.godeltech.springgodelbot.util.ConstantUtil.NO_FOUND_PATTERN_WITHOUT_VALUES;

@Getter
public class ResourceNotFoundException extends RuntimeException {
    private Message botMessage;
    private User user;

    public ResourceNotFoundException(Class<?> resourceType, String fieldName, Object fieldValue, Message botMessage, User user) {
        super(String.format(NO_FOUND_PATTERN, resourceType, fieldName, fieldValue));
        this.botMessage = botMessage;
        this.user=user;
    }

    public ResourceNotFoundException(Class<?> resourceType, Message botMessage, User user) {
        super(String.format(NO_FOUND_PATTERN_WITHOUT_VALUES, resourceType));
        this.botMessage = botMessage;
        this.user=user;
    }
}
