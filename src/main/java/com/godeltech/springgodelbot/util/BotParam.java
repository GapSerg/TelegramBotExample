package com.godeltech.springgodelbot.util;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BotParam {
    private String token;
    private String botName;
}
