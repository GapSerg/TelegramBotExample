package com.godeltech.springgodelbot.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.service.CallbackResolverService;
import com.godeltech.springgodelbot.service.MessageResolverService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import com.google.common.cache.CacheBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Configuration
@Data
@EnableScheduling
@EnableCaching
public class AppConfig {

    private final BotProp botProp;

    @Value("${cache.expire}")
    private String cacheExpire;

    @Value("${cache.size}")
    private String cacheSize;

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper;
    }

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

    @Bean
    public SetWebhook setWebhookInstance() {
        return SetWebhook.builder()
                .url(botProp.getWebHookPath())
                .build();
    }

    @Bean
    @SneakyThrows
    public TudaSudaTelegramBot tudaSudaTelegramBot(SetWebhook setWebhookInstance, MessageResolverService messageResolverService,
                                                   CallbackResolverService callbackResolverService, TokenService tokenService) {
        TudaSudaTelegramBot tudaSudaTelegramBot =
                new TudaSudaTelegramBot(setWebhookInstance, messageResolverService, callbackResolverService, tokenService);
        tudaSudaTelegramBot.setBotUsername(botProp.getName());
        tudaSudaTelegramBot.setBotToken(botProp.getToken());
        tudaSudaTelegramBot.setBotPath(botProp.getWebHookPath());
        tudaSudaTelegramBot.setChmokiId(botProp.getChmokiId());
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(tudaSudaTelegramBot, setWebhookInstance);
        return tudaSudaTelegramBot;

    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager() {
            @Override
            protected Cache createConcurrentMapCache(String name) {
                return new ConcurrentMapCache(name, CacheBuilder.newBuilder()
                        .expireAfterWrite(Integer.parseInt(cacheExpire), TimeUnit.HOURS)
                        .maximumSize(Integer.parseInt(cacheSize))
                        .build().asMap(), false);
            }
        };
    }

    @Bean
    public Map<String, PassengerRequest> passengerRequests() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, DriverRequest> driverRequests() {
        return new ConcurrentHashMap<>();
    }

    @Bean
    public Map<String, ChangeOfferRequest> changeOfferRequests() {
        return new ConcurrentHashMap<>();
    }


}
