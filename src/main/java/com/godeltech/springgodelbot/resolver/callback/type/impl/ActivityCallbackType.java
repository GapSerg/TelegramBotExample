package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_ROLE;

@Component
public class ActivityCallbackType implements CallbackType {


    private final RequestService requestService;
    private final CityService cityService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public ActivityCallbackType(RequestService requestService, CityService cityService,
                                @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.cityService = cityService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public String getCallbackName() {
        return ACTIVITY.name();
    }

    @Override
    @SneakyThrows
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        var activityType = Activity.valueOf(getCallbackValue(callbackQuery.getData()));
        var cities = cityService.findAll();
        User user = callbackQuery.getFrom();
        Set<Integer> messages = new HashSet<>();
        messages.add(callbackQuery.getMessage().getMessageId());
        switch (activityType) {
            case DRIVER:
                requestService.saveDriverRequest(DriverRequest.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .userDto(UserDto.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .build())
                        .cities(new ArrayList<>())
                        .messages(messages)
                        .build());
                tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_ROLE,activityType));
                return RouteUtil.createRouteSendMessage(cities, DRIVER_ROUTE, callbackQuery.getMessage().getChatId());
            case PASSENGER:
                requestService.savePassengerRequest(PassengerRequest.builder()
                        .chatId(callbackQuery.getMessage().getChatId())
                        .userDto(UserDto.builder()
                                .id(user.getId())
                                .userName(user.getUserName())
                                .firstName(user.getFirstName())
                                .lastName(user.getLastName())
                                .build())
                        .cities(new ArrayList<>())
                        .messages(messages)
                        .build());
                tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_ROLE,activityType));
                return RouteUtil.createRouteSendMessage(cities, PASSENGER_ROUTE, callbackQuery.getMessage().getChatId());
            default:
                throw new RuntimeException("There is no such activity");
        }
    }
}

