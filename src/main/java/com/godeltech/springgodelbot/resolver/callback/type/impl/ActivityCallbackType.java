package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.*;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_ROLE;

@Component
@Slf4j
public class ActivityCallbackType implements CallbackType {


    private final RequestService requestService;
    private final CityService cityService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final TokenService tokenService;

    public ActivityCallbackType(RequestService requestService,
                                CityService cityService,
                                @Lazy TudaSudaTelegramBot tudaSudaTelegramBot,
                                TokenService tokenService) {
        this.requestService = requestService;
        this.cityService = cityService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.tokenService = tokenService;
    }

    @Override
    public Integer getCallbackName() {
        return ACTIVITY.ordinal();
    }

    @Override
    @SneakyThrows
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token  = getCallbackToken(callbackQuery.getData());
        tokenService.checkIncomeToken(token,callbackQuery.getMessage());
        Activity activityType = Activity.valueOf(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with activity :{} and token : {}",ACTIVITY,activityType,token);
        List<City> cities = cityService.findAll();
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
                        .build(),token );
                return createRouteSendMessage(cities, DRIVER_ROUTE.ordinal(),CANCEL_DRIVER_REQUEST.ordinal(),
                        callbackQuery.getMessage(),token,String.format(CHOSEN_ROLE,activityType));
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
                        .build(), token);
//                tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(CHOSEN_ROLE, activityType));
                return createRouteSendMessage(cities, PASSENGER_ROUTE.ordinal(),CANCEL_PASSENGER_REQUEST.ordinal(),
                        callbackQuery.getMessage(), token,String.format(CHOSEN_ROLE,activityType));
            default:
                throw new RuntimeException("There is no such activity");
        }
    }
}

