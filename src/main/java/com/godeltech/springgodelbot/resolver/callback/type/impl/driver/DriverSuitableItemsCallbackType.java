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

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.DRIVER_SUITABLE_ITEM;
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.getCurrentSuitableActivities;
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.makeEditMessageTextForSuitableItems;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class DriverSuitableItemsCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return DRIVER_SUITABLE_ITEM.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        Activity activity = Activity.valueOf(getCallbackValue(callbackQuery.getData()));
        log.info("Get {} callback type with token : {} with activity :{} by user : {} ",
                DRIVER_SUITABLE_ITEM, token, activity, callbackQuery.getFrom().getUserName());
        Request request = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        request.getSuitableActivities().add(activity);
        String.format(CHOOSE_THE_SUITABLE_ACTIVITIES, request.getActivity().getTextMessage()
                , getCurrentRoute(request.getCities()));
        String textMessage = String.format(CHOSE_ONE_MORE_SUITABLE_ACTIVITY,request.getActivity().getTextMessage(),
                getCurrentRoute(request.getCities()),getCurrentSuitableActivities(request.getSuitableActivities()));
        request = requestService.updateRequest(request, callbackQuery.getMessage(), callbackQuery.getFrom());
        return makeEditMessageTextForSuitableItems(callbackQuery.getMessage(), request.getSuitableActivities(),
                Activity.DRIVER, request.getToken().getId(),textMessage);
    }


}
