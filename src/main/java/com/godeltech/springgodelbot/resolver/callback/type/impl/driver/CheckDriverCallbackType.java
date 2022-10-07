package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

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
    private final TokenService tokenService;

    public CheckDriverCallbackType(RequestService requestService,
                                   @Lazy TudaSudaTelegramBot tudaSudaTelegramBot,
                                   TokenService tokenService) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.tokenService = tokenService;
    }

    @Override
    public Integer getCallbackName() {
        return CHECK_DRIVER_REQUEST.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
       String token = getCallbackToken(callbackQuery.getData());
        log.info("Got callback type :{} with token :{}", CHECK_DRIVER_REQUEST, token);
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage(),token );
        checkUsername(callbackQuery, driverRequest,token );
        driverRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        driverRequest.setNeedForDescription(true);
        requestService.clearChangeOfferRequestsAndPassengerRequests(token);
        return createEditMessageTextAfterConfirm(callbackQuery, SAVE_DRIVER_WITHOUT_DESCRIPTION.ordinal(),
                WRITE_ADD_DESCRIPTION_FOR_DRIVER,token);
    }

    private void checkUsername(CallbackQuery callbackQuery, DriverRequest driverRequest, String token) {
        if (callbackQuery.getFrom().getUserName() == null){
            tokenService.deleteToken(token);
            tudaSudaTelegramBot.deleteMessages(driverRequest.getChatId(), driverRequest.getMessages());
            throw new UserAuthorizationException(UserDto.class, "username", null, callbackQuery.getMessage(),false );}
    }
}
