package com.godeltech.springgodelbot.exception;

import lombok.Getter;

import static com.godeltech.springgodelbot.util.ConstantUtil.NO_FOUND_PATTERN;
@Getter
public class ResourceNotFoundException extends RuntimeException{
    private Long chatId;
    public ResourceNotFoundException(Class<?> resourceType,String fieldName, Object fieldValue,Long chatId){
        super(String.format(NO_FOUND_PATTERN,resourceType,fieldName,fieldValue));
        this.chatId=chatId;
    }
}
