package com.godeltech.springgodelbot.resolver.callback.type.impl.driver;

import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADD_DESCRIPTION_FOR_DRIVER;

@Component
@RequiredArgsConstructor
@Slf4j
public class CheckDriverCallbackType implements CallbackType {
    private final RequestService requestService;
    @Override
    public String getCallbackName() {
        return CHECK_DRIVER_REQUEST.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got callback type :{} from user :{}", CHECK_DRIVER_REQUEST,callbackQuery.getFrom().getUserName());
        DriverRequest driverRequest = requestService.getDriverRequest(callbackQuery.getMessage());
        if (driverRequest.getUserDto().getUserName()==null)
            throw new UserAuthorizationException(UserDto.class,"username",null, callbackQuery.getMessage());
        driverRequest.getMessages().add(callbackQuery.getMessage().getMessageId());
        driverRequest.setNeedForDescription(true);
        requestService.clearChangeOfferRequestsAndPassengerRequests(callbackQuery.getMessage().getChatId());
        return createEditMessageTextAfterConfirm(callbackQuery, SAVE_DRIVER_WITHOUT_DESCRIPTION,
                WRITE_ADD_DESCRIPTION_FOR_DRIVER);
    }
}
