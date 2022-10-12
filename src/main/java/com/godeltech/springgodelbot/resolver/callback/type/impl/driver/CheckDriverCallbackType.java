package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHECK_DRIVER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SAVE_DRIVER_WITHOUT_DESCRIPTION;
import static com.godeltech.springgodelbot.util.CallbackUtil.createEditMessageTextAfterConfirm;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADD_DESCRIPTION_FOR_DRIVER;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckDriverCallbackType implements CallbackType {
    private final RequestService requestService;
    private final UserService userService;

    @Override
    public Integer getCallbackName() {
        return CHECK_DRIVER_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
       String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback type :{} with token :{} by user : {}",
                CHECK_DRIVER_REQUEST, token,callbackQuery.getFrom().getUserName());
        Request driverRequest = requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() );
        userService.userAuthorization(callbackQuery.getFrom(),callbackQuery.getMessage(),false);
        requestService.prepareRequestForDescription(driverRequest);
        return createEditMessageTextAfterConfirm(callbackQuery, SAVE_DRIVER_WITHOUT_DESCRIPTION.ordinal(),
                WRITE_ADD_DESCRIPTION_FOR_DRIVER,driverRequest.getToken().getId());
    }
}
