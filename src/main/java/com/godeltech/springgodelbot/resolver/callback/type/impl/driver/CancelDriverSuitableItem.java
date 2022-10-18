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

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CANCEL_DRIVER_SUITABLE_ITEM;
import static com.godeltech.springgodelbot.util.CallbackUtil.ActivityUtil.makeEditMessageTextForSuitableItems;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@RequiredArgsConstructor
@Slf4j
public class CancelDriverSuitableItem implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CANCEL_DRIVER_SUITABLE_ITEM.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        Activity activity = Activity.valueOf(getCallbackValue(callbackQuery.getData()));
        log.info("Get {} callback type with activity :{} with token : {} by user : {}",
                CANCEL_DRIVER_SUITABLE_ITEM, activity, token, callbackQuery.getFrom().getUserName());
        Request request = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        request.getSuitableActivities().remove(activity);
        request = requestService.updateRequest(request,callbackQuery.getMessage(),callbackQuery.getFrom());
        return makeEditMessageTextForSuitableItems(callbackQuery.getMessage(),request.getSuitableActivities(),
                Activity.DRIVER,request.getToken().getId());
    }
}
