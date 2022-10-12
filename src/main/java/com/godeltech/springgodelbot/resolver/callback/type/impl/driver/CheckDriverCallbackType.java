package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.User;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHECK_DRIVER_REQUEST;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.SAVE_DRIVER_WITHOUT_DESCRIPTION;
import static com.godeltech.springgodelbot.util.CallbackUtil.createEditMessageTextAfterConfirm;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADD_DESCRIPTION_FOR_DRIVER;

@Component
@Slf4j
public class CheckDriverCallbackType implements CallbackType {
    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public CheckDriverCallbackType(RequestService requestService,
                                   @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

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
        checkUsername(callbackQuery, driverRequest);
        requestService.prepareRequestForDescription(driverRequest);
        return createEditMessageTextAfterConfirm(callbackQuery, SAVE_DRIVER_WITHOUT_DESCRIPTION.ordinal(),
                WRITE_ADD_DESCRIPTION_FOR_DRIVER,driverRequest.getToken().getId());
    }

    private void checkUsername(CallbackQuery callbackQuery, Request driverRequest) {
        if (callbackQuery.getFrom().getUserName() == null){
            requestService.deleteRequest(driverRequest,callbackQuery.getMessage());
            tudaSudaTelegramBot.deleteMessage(driverRequest.getToken().getChatId(), driverRequest.getToken().getMessageId());
            throw new UserAuthorizationException(User.class, "username", null, callbackQuery.getMessage(),false );}
    }
}
