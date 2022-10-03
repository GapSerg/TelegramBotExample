package com.godeltech.springgodelbot.resolver.callback.type.impl.passenger;

import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_PASSENGER_ROUTE;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.PASSENGER_ROUTE;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.createEditSendMessageForRoutes;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelPassengerRouteCallbackType implements CallbackType {
    private final CityService cityService;
    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return Callbacks.CANCEL_PASSENGER_ROUTE.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        var routeId = Integer.parseInt(getCallbackValue(callbackQuery.getData()));
        log.info("Callback data with type: {} and routeId: {}", PASSENGER_ROUTE, routeId);
        List<City> cities = cityService.findAll();
        var reservedRoute = cities.stream()
                .filter(route -> route.getId().equals(routeId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        PassengerRequest passengerRequest = requestService.getPassengerRequest(callbackQuery.getMessage());
        passengerRequest.getCities().remove(reservedRoute);
        return createEditSendMessageForRoutes(callbackQuery, cities, passengerRequest.getCities(),
                PASSENGER_ROUTE, CANCEL_PASSENGER_ROUTE);
    }
}
