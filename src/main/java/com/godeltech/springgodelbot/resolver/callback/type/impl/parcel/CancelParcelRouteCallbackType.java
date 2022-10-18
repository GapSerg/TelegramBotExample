package com.godeltech.springgodelbot.resolver.callback.type.impl.parcel;

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
public class CancelParcelRouteCallbackType implements CallbackType {
    private final CityService cityService;
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CANCEL_PARCEL_ROUTE.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        int routeId = Integer.parseInt(getCallbackValue(callbackQuery.getData()));
        log.info("Callback data with type: {} and routeId: {} and token: {} by user : {} ",
                CANCEL_PARCEL_ROUTE, routeId, token, callbackQuery.getFrom());
        List<City> cities = cityService.findAll();
        City reservedRoute = cities.stream()
                .filter(route -> route.getId().equals(routeId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException(City.class, callbackQuery.getMessage(), callbackQuery.getFrom()));
        Request parcelRoute = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        List<String> reservedCities = parcelRoute.getCities();
        reservedCities.remove(reservedRoute.getName());
        parcelRoute = requestService.updateRequest(parcelRoute, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = parcelRoute.getCities().isEmpty() ?
                String.format(CHOSE_THE_ROUTE, parcelRoute.getActivity()) :
                String.format(CURRENT_ROUTE, parcelRoute.getActivity(), getCurrentRoute(reservedCities));
        return createEditSendMessageForRoutes(callbackQuery, cities, reservedCities,
                PARCEL_ROUTE.ordinal(), CANCEL_PARCEL_ROUTE.ordinal(), CANCEL_PARCEL_REQUEST.ordinal(),
                parcelRoute.getToken().getId(), textMessage);
    }
}
