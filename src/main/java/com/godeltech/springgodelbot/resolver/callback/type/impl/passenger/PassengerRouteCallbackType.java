package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.createEditSendMessageForRoutes;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.CURRENT_ROUTE;

@Component
@RequiredArgsConstructor
@Slf4j
public class PassengerRouteCallbackType implements CallbackType {

    private final RequestService requestService;
    private final CityService cityService;

    @Override
    public Integer getCallbackName() {
        return PASSENGER_ROUTE.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        int routeId = Integer.parseInt(getCallbackValue(callbackQuery.getData()));
        log.info("Callback data with type: {} and routeId: {} and with token: {}", PASSENGER_ROUTE, routeId, token);
        List<City> cities = cityService.findAll();
        City reservedRoute = cities.stream()
                .filter(route -> route.getId().equals(routeId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage(), token);
        passengerRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        List<City> reservedCities = passengerRequest.getCities();
        if (reservedCities.size() > 1)
            reservedCities.remove(1);
        reservedCities.add(reservedRoute);
        return createEditSendMessageForRoutes(callbackQuery, cities, reservedCities,
                PASSENGER_ROUTE.ordinal(), CANCEL_PASSENGER_ROUTE.ordinal(), CANCEL_PASSENGER_REQUEST.ordinal(), token,
                String.format(CURRENT_ROUTE, passengerRequest.getActivity(), getCurrentRoute(reservedCities)));
    }

}