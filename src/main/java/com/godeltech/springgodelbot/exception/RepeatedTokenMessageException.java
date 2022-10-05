package com.godeltech.springgodelbot.exception;

public class RepeatedTokenMessageException extends RuntimeException{
    public RepeatedTokenMessageException(String token) {
        super(String.format("There was an answer on the message with id : %s",token));
    }
}
