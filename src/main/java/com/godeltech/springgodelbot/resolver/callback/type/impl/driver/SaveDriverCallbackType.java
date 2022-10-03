package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.ConstantUtil.SUCCESSFUL_REQUEST_SAVING;

@Component
@Slf4j
public class SaveDriverCallbackType implements CallbackType {


    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public SaveDriverCallbackType(RequestService requestService,
                                  @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public String getCallbackName() {
        return Callbacks.SAVE_DRIVER_WITHOUT_DESCRIPTION.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got save supplier callback type without description with chatId:{}"
                ,callbackQuery.getMessage().getChatId());
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage());
        driverRequest.getMessages()
                .add(callbackQuery.getMessage().getMessageId());
        tudaSudaTelegramBot.deleteMessages(callbackQuery.getMessage().getChatId(), driverRequest.getMessages());
        requestService.saveDriver(callbackQuery.getMessage());
        return getStartMenu(callbackQuery.getMessage().getChatId(), SUCCESSFUL_REQUEST_SAVING);
    }
}
