package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.BotMenu;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;
import java.util.stream.Collectors;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.FINISH_CHANGING_ROUTE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

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
        List<? extends Request> requests = changeDriverRequest.getActivity() == Activity.DRIVER ?
               requestService.findPassengersByRequestData(changeDriverRequest) :
               requestService.findDriversByRequestData(changeDriverRequest);
        return getAvailableOffersList(requests,callbackQuery, ROUTE_CHANGED);
    }


}
