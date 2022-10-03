package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.createEditSendMessageForRoutes;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeRouteOfOfferCallbackType implements CallbackType {

    private final CityService cityService;
    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return CHANGE_ROUTE_OF_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String[] data = callbackQuery.getData().split(SPLITTER);
        if (callbackQuery.getFrom().getUserName()==null)
            throw new UserAuthorizationException(UserDto.class,"username",null, callbackQuery.getMessage());
        ChangeDriverRequest changeDriverRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage());
        log.info("Callback data with type: {}", CHANGE_ROUTE_OF_OFFER);
        var reservedRoutes = changeDriverRequest.getCities();
        List<City> cities = cityService.findAll();
        if (data.length > 1) {
                var routeId = Integer.parseInt(data[1]);
                cities.stream()
                        .filter(route -> route.getId().equals(routeId))
                        .forEach(reservedRoutes::add);
        }
        return createEditSendMessageForRoutes(callbackQuery, cities, reservedRoutes,
                CHANGE_ROUTE_OF_OFFER, CANCEL_ROUTE_OF_OFFER);
    }
}
