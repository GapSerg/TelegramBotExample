package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.time.LocalDate;

import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;

@Component
@RequiredArgsConstructor
public class CancelFirstDateDriverCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return Callbacks.CANCEL_FIRST_DATE_DRIVER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        LocalDate canceledDate = LocalDate.parse(getCallbackValue(callbackQuery.getData()));
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage());
        driverRequest.setFirstDate(null);
        return CallbackUtil.DateUtil.createEditMessageTextForFirstDate(callbackQuery, Callbacks.FIRST_DATE_DRIVER.name(),
                "You've canceled the first date", canceledDate);
    }
}
