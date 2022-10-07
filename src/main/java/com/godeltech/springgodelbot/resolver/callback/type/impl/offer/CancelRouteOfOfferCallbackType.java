package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.City;
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
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelRouteOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;
    private final CityService cityService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.CANCEL_ROUTE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        int routeId = Integer.parseInt(getCallbackValue(callbackQuery.getData()));
        log.info("Callback data with type: {} and routeId: {} and token : {}", CANCEL_ROUTE_OF_OFFER, routeId,token);
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(),token );
        List<City> cities = cityService.findAll();
        City reservedRoute = cities.stream()
                .filter(route -> route.getId().equals(routeId))
                .findFirst()
                .orElseThrow(RuntimeException::new);
        List<City> reservedCities = changeOfferRequest.getCities();
        reservedCities.remove(reservedRoute);
        String textMessage = reservedCities.isEmpty() ?
               CHOSE_THE_ROUTE_OF_OFFER:
                String.format(CURRENT_ROUTE_OF_OFFER, getCurrentRoute(reservedCities));
        return createEditSendMessageForRoutes(callbackQuery, cities, reservedCities,
                CHANGE_ROUTE_OF_OFFER.ordinal(), CANCEL_ROUTE_OF_OFFER.ordinal(),
                RETURN_TO_CHANGE_OF_OFFER.ordinal(),token,textMessage);
    }
}
