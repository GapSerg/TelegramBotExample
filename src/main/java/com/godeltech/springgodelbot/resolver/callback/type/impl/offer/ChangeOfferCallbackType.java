package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.OfferService;
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
    public String getCallbackName() {
        return CHANGE_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        long offerId = Long.parseLong(getCallbackValue(callbackQuery.getData()));
        ChangeDriverRequest request = requestService.addNewChangeOfferRequest(offerId, callbackQuery.getMessage().getChatId());
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(getOffersView(request))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(getChangeOfferButtons(request))
                        .build())
                .build();
    }

    private List<List<InlineKeyboardButton>> getChangeOfferButtons(ChangeDriverRequest request) {
        return List.of(List.of(InlineKeyboardButton.builder()
                                .text("Change route")
                                .callbackData(CHANGE_ROUTE_OF_OFFER.name())
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Change date")
                                .callbackData(CHANGE_DATE_OF_OFFER.name())
                                .build()
                ),
                List.of(InlineKeyboardButton.builder()
                        .text("Change description")
                        .callbackData(CHANGE_DESCRIPTION_OF_OFFER.name())
                        .build(), InlineKeyboardButton.builder()
                        .text("Delete offer")
                        .callbackData(DELETE_OFFER.name() + SPLITTER + request.getOfferId())
                        .build()),
                List.of(InlineKeyboardButton.builder()
                        .text("Back to offer list")
                        .callbackData(MY_OFFERS.name() + SPLITTER + request.getActivity())
                        .build()));
    }
}
