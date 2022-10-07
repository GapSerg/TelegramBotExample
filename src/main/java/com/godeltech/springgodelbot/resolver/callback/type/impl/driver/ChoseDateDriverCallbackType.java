package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createSendMessageForFirstDate;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_FIRST_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.SELECTED_ROUTE;

@Component
@Slf4j
public class ChoseDateDriverCallbackType implements CallbackType {

    private final TudaSudaTelegramBot tudaSudaTelegramBot;
    private final RequestService requestService;

    public ChoseDateDriverCallbackType(@Lazy TudaSudaTelegramBot tudaSudaTelegramBot,
                                       RequestService requestService) {
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.requestService = requestService;
    }

    @Override
    public Integer getCallbackName() {
        return CHOSE_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Callback data with type: {} with token : {}", CHOSE_DATE_DRIVER, token);
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage(),token );
        String selectedRoute = getCurrentRoute(driverRequest.getCities());
        driverRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
//        tudaSudaTelegramBot.editPreviousMessage(callbackQuery, String.format(SELECTED_ROUTE, selectedRoute));
        String textMessage = String.format(CHOOSE_THE_FIRST_DATE,driverRequest.getActivity()
                ,getCurrentRoute(driverRequest.getCities()));
        return createSendMessageForFirstDate(callbackQuery.getMessage(), FIRST_DATE_DRIVER.ordinal(),
                CANCEL_DRIVER_REQUEST.ordinal(),textMessage, token);
    }
}
