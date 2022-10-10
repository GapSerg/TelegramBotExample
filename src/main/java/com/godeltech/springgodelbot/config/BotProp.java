package com.godeltech.springgodelbot.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@Component
@ConfigurationProperties(prefix = "bot")
@Getter
@Setter
public class BotProp {
    private String webHookPath;
    private String name;
    private String token;
    private int days;
    private String chmokiId;
}
