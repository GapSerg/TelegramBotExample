package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@Slf4j
@RequiredArgsConstructor
public class CancelFirstDateDriverCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.CANCEL_FIRST_DATE_DRIVER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} type with token : {}",Callbacks.CANCEL_FIRST_DATE_DRIVER,token);
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage(), token);
        driverRequest.setFirstDate(null);
        return CallbackUtil.DateUtil.createEditMessageTextForFirstDate(callbackQuery, Callbacks.FIRST_DATE_DRIVER.ordinal(),
                "You've canceled the first date", canceledDate,token );
    }
}
