package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
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
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.List;
import java.util.Set;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_DESCRIPTION_OF_OFFER;
import static com.godeltech.springgodelbot.resolver.callback.Callbacks.RETURN_TO_CHANGE_OF_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCancelButton;
import static com.godeltech.springgodelbot.util.ConstantUtil.WRITE_ADDITIONAL_DESCRIPTION_FOR_CHANGE;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeDescriptionOfOfferCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CHANGE_DESCRIPTION_OF_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        if (callbackQuery.getFrom().getUserName() == null)
            throw new UserAuthorizationException(UserDto.class, "username", null, callbackQuery.getMessage(), false);
        ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(callbackQuery.getMessage(), token);
        log.info("Change description of offer with id: {} and token : {}", changeOfferRequest.getOfferId(), token);
        requestService.clearDriverRequestsAndPassengerRequests(token);
        changeOfferRequest.setNeedForDescription(true);
        changeOfferRequest.setMessages(Set.of(callbackQuery.getMessage().getMessageId()));
        return EditMessageText.builder()
                .text(WRITE_ADDITIONAL_DESCRIPTION_FOR_CHANGE)
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(getCancelButton(RETURN_TO_CHANGE_OF_OFFER.ordinal(), token, "Return to offer"))))
                        .build())
                .build();
    }
}
