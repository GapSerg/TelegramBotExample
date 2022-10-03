package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.exception.UserAuthorizationException;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.Set;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_DESCRIPTION_OF_OFFER;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADDITIONAL_DESCRIPTION_FOR_CHANGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeDescriptionOfOfferCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return CHANGE_DESCRIPTION_OF_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        if (callbackQuery.getFrom().getUserName() == null)
            throw new UserAuthorizationException(UserDto.class, "username", null, callbackQuery.getMessage(), false);
        ChangeDriverRequest changeDriverRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage());
        log.info("Change description of offer with id: {}", changeDriverRequest.getOfferId());
        requestService.clearDriverRequestsAndPassengerRequests(callbackQuery.getMessage().getChatId());
        changeDriverRequest.setNeedForDescription(true);
        changeDriverRequest.setMessages(Set.of(callbackQuery.getMessage().getMessageId()));
        return EditMessageText.builder()
                .text(WRITE_ADDITIONAL_DESCRIPTION_FOR_CHANGE)
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .build();
    }
}
