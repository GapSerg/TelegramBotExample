package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.createEditSendMessageForRoutes;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.CURRENT_ROUTE_OF_OFFER;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeRouteOfOfferCallbackType implements CallbackType {

    private final CityService cityService;
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CHANGE_ROUTE_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(SPLITTER);
        String token = getCallbackToken(callbackQuery.getData());
        if (callbackQuery.getFrom().getUserName() == null)
            throw new UserAuthorizationException(User.class, "username", null, callbackQuery.getMessage(), false);
        Request changeOfferRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        log.info("Callback data with type: {} with token: {} by user : {}",
                CHANGE_ROUTE_OF_OFFER, token, callbackQuery.getFrom().getUserName());
        List<String> reservedCities = changeOfferRequest.getCities();
        List<City> cities = cityService.findAll();
        if (data.length > 2) {
            int routeId = Integer.parseInt(data[2]);
            cities.stream()
                    .filter(city -> city.getId().equals(routeId))
                    .forEach(city -> {
                        if (changeOfferRequest.getActivity() == Activity.PASSENGER && reservedCities.size() > 1)
                            checkReservedCitiesForPassenger(reservedCities);
                        reservedCities.add(city.getName());
                    });
        }
        Request updatedRequest = requestService.updateRequest(changeOfferRequest, callbackQuery.getMessage(), callbackQuery.getFrom());
        return createEditSendMessageForRoutes(callbackQuery, cities, updatedRequest.getCities(),
                CHANGE_ROUTE_OF_OFFER.ordinal(), CANCEL_ROUTE_OF_OFFER.ordinal(), RETURN_TO_CHANGE_OF_OFFER.ordinal(),
                updatedRequest.getToken().getId(),
                String.format(CURRENT_ROUTE_OF_OFFER, getCurrentRoute(updatedRequest.getCities())));
    }

    private void checkReservedCitiesForPassenger(List<String> reservedCities) {
        if (reservedCities.size() > 1) {
            reservedCities.remove(1);
        }
    }
}
