package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.ArrayList;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHOOSE_DRIVER_SUITABLE_ITEM;
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.makeEditMessageTextForSuitableItems;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_SUITABLE_ACTIVITIES;

@Component
@Slf4j
@RequiredArgsConstructor
public class ChooseDriverSuitableItem implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CHOOSE_DRIVER_SUITABLE_ITEM.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Get : {} callback type with token : {} by user : {}",
                CHOOSE_DRIVER_SUITABLE_ITEM, token, callbackQuery.getFrom().getUserName());
        Request request = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        request.setSuitableActivities(new ArrayList<>());
        request = requestService.updateRequest(request, callbackQuery.getMessage(), callbackQuery.getFrom());
        String textMessage = String.format(CHOOSE_THE_SUITABLE_ACTIVITIES, request.getActivity().getTextMessage()
                , getCurrentRoute(request.getCities()));
        return makeEditMessageTextForSuitableItems(callbackQuery.getMessage(), Activity.DRIVER, request.getToken().getId(),textMessage);
    }
}
