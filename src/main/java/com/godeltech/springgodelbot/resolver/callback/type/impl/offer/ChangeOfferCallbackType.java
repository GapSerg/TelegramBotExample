package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChangeOfferCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return CHANGE_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        long offerId = Long.parseLong(getCallbackValue(callbackQuery.getData()));
        log.info("Got {} callback type with route id :{} and token: {}", CHANGE_OFFER,offerId,token);
        ChangeOfferRequest request = requestService.addNewChangeOfferRequest(offerId, callbackQuery.getMessage().getChatId(),token);
        return getEditTextMessageForOffer(callbackQuery, token, request);
    }




}
