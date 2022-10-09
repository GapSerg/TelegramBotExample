package com.godeltech.springgodelbot.exception;

import static com.godeltech.springgodelbot.util.ConstantUtil.NO_UNIQUE_PATTERN;

public class ResourceNotUniqueException extends RuntimeException {
    public ResourceNotUniqueException(Class<?> resourceType, String fieldName, Object fieldValue) {
        super(String.format(NO_UNIQUE_PATTERN,resourceType,fieldName,fieldValue));
    }
}
