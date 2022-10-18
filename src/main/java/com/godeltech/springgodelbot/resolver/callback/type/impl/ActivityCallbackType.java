package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.DriverRequest;
import com.godeltech.springgodelbot.model.entity.ParcelRequest;
import com.godeltech.springgodelbot.model.entity.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;
import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.createRouteEditMessageText;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSEN_ROLE;

@Component
@Slf4j
public class ActivityCallbackType implements CallbackType {


    private final RequestService requestService;
    private final CityService cityService;


    public ActivityCallbackType(RequestService requestService,
                                CityService cityService) {
        this.requestService = requestService;
        this.cityService = cityService;
    }

    @Override
    public Integer getCallbackName() {
        return ACTIVITY.ordinal();
    }

    @Override
    @SneakyThrows
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        Activity activityType = Activity.valueOf(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with activity :{} and token : {} by user : {}", ACTIVITY, activityType, token,
                callbackQuery.getFrom().getUserName());
        List<City> cities = cityService.findAll();
        switch (activityType) {
            case DRIVER:
                requestService.saveRequest(DriverRequest.builder()
                        .cities(new ArrayList<>())
                        .build(), token, callbackQuery.getMessage(), callbackQuery.getFrom());
                return createRouteEditMessageText(cities, DRIVER_ROUTE.ordinal(), CANCEL_DRIVER_REQUEST.ordinal(),
                        callbackQuery.getMessage(), token, String.format(CHOSEN_ROLE, activityType.getTextMessage()));
            case PASSENGER:
                requestService.saveRequest(PassengerRequest.builder()
                        .cities(new ArrayList<>())
                        .build(), token, callbackQuery.getMessage(), callbackQuery.getFrom());
                return createRouteEditMessageText(cities, PASSENGER_ROUTE.ordinal(), CANCEL_PASSENGER_REQUEST.ordinal(),
                        callbackQuery.getMessage(), token, String.format(CHOSEN_ROLE, activityType.getTextMessage()));
            case PARCEL:
                requestService.saveRequest(ParcelRequest.builder()
                                .cities(new ArrayList<>())
                                .build(), token, callbackQuery.getMessage(), callbackQuery.getFrom());
                return createRouteEditMessageText(cities,PARCEL_ROUTE.ordinal(),CANCEL_PARCEL_REQUEST.ordinal(),
                        callbackQuery.getMessage(),token,String.format(CHOSEN_ROLE,activityType.getTextMessage()));
            default:
                throw new RuntimeException("There is no such activity");
        }
    }
}

