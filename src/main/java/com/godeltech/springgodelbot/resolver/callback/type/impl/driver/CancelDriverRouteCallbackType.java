package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
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
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOSE_THE_ROUTE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CURRENT_ROUTE;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDriverRouteCallbackType implements CallbackType {
    private final CityService cityService;
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.CANCEL_DRIVER_ROUTE.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        int routeId = Integer.parseInt(getCallbackValue(callbackQuery.getData()));
        log.info("Callback data with type: {} and routeId: {} and token : {} by user : {}",
                DRIVER_ROUTE, routeId, token, callbackQuery.getFrom().getUserName());
        List<City> cities = cityService.findAll();
        City reservedRoute = cities.stream()
                .filter(route -> route.getId().equals(routeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(City.class, callbackQuery.getMessage(), callbackQuery.getFrom()));
        Request driverRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        List<String> reservedCities = driverRequest.getCities();
        reservedCities.remove(reservedRoute.getName());
        driverRequest = requestService.updateRequest(driverRequest, callbackQuery.getMessage(),callbackQuery.getFrom() );
        String textMessage = reservedCities.isEmpty() ?
                String.format(CHOSE_THE_ROUTE, driverRequest.getActivity()) :
                String.format(CURRENT_ROUTE, driverRequest.getActivity(), getCurrentRoute(reservedCities));
        return createEditSendMessageForRoutes(callbackQuery, cities, reservedCities,
                DRIVER_ROUTE.ordinal(), CANCEL_DRIVER_ROUTE.ordinal(), CANCEL_DRIVER_REQUEST.ordinal(),
                driverRequest.getToken().getId(), textMessage);
    }
}
