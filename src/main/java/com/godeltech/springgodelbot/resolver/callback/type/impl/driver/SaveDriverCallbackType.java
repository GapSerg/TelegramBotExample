package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.SUCCESSFUL_REQUEST_SAVING;

@Component
@Slf4j
public class SaveDriverCallbackType implements CallbackType {


    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final TokenService tokenService;

    public SaveDriverCallbackType(RequestService requestService,
                                  @Lazy TudaSudaTelegramBot tudaSudaTelegramBot, TokenService tokenService) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.tokenService = tokenService;
    }

    @Override
    public Integer getCallbackName() {
        return SAVE_DRIVER_WITHOUT_DESCRIPTION.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} callback type without description with token:{}",SAVE_DRIVER_WITHOUT_DESCRIPTION
                , token);
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage(),token );
        driverRequest.getMessages()
                .add(callbackQuery.getMessage().getMessageId());
        tudaSudaTelegramBot.deleteMessages(callbackQuery.getMessage().getChatId(), driverRequest.getMessages());
        requestService.saveDriver(driverRequest,token);
        return getStartMenu(callbackQuery.getMessage().getChatId(), SUCCESSFUL_REQUEST_SAVING, tokenService.createToken());
    }
}
