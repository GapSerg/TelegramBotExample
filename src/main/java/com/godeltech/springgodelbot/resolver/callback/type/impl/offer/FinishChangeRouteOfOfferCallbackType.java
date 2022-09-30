package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.BotMenu;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.FINISH_CHANGING_ROUTE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class FinishChangeRouteOfOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return FINISH_CHANGING_ROUTE_OF_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got callback type :{}, by user :{}", FINISH_CHANGING_ROUTE_OF_OFFER, callbackQuery.getFrom().getUserName());
        ChangeDriverRequest changeDriverRequest =
                requestService.getChangeOfferRequest(callbackQuery.getMessage());
        requestService.updateRouteOfOffer(changeDriverRequest);
        String text = changeDriverRequest.getActivity() == Activity.DRIVER ?
                getListOfRequests(requestService.findPassengersByRequestData(changeDriverRequest)) :
                getListOfRequests(requestService.findDriversByRequestData(changeDriverRequest));
        return BotMenu.getStartMenu(callbackQuery.getMessage(), "You've successfully changed route of offer" + text);
    }
}
