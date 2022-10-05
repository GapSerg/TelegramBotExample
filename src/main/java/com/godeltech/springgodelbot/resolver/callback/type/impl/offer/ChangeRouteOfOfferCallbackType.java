package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
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

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_ROUTE_OF_OFFER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_ROUTE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.createEditSendMessageForRoutes;
import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;

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
            throw new UserAuthorizationException(UserDto.class, "username", null, callbackQuery.getMessage(),false );
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(),token );
        log.info("Callback data with type: {} with token: {}", CHANGE_ROUTE_OF_OFFER,token);
        List<City> reservedRoutes = changeOfferRequest.getCities();
        List<City> cities = cityService.findAll();
        if (data.length > 2) {
            int routeId = Integer.parseInt(data[2]);
            cities.stream()
                    .filter(route -> route.getId().equals(routeId))
                    .forEach(reservedRoutes::add);
        }
        return createEditSendMessageForRoutes(callbackQuery, cities, reservedRoutes,
                CHANGE_ROUTE_OF_OFFER.ordinal(), CANCEL_ROUTE_OF_OFFER.ordinal(),token );
    }
}
