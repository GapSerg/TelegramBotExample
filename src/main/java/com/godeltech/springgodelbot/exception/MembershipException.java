package com.godeltech.springgodelbot.exception;

import lombok.Getter;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.godeltech.springgodelbot.util.ConstantUtil.MEMBERSHIP_PATTERN;

@Getter
public class MembershipException extends RuntimeException {

    private Message botMessage;
    private boolean isFromMessage;

    public MembershipException(Long userId, String username, Message botMessage,boolean isFromMessage) {
        super(String.format(MEMBERSHIP_PATTERN, userId, username));
        this.botMessage = botMessage;
        this.isFromMessage=isFromMessage;
    }
}
