package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
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
import java.util.stream.Collectors;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackValue;
import static com.godeltech.springgodelbot.util.ConstantUtil.OFFERS_OF_DRIVERS_PATTERN;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyOffersCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public String getCallbackName() {
        return Callbacks.MY_OFFERS.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        Activity activity= Activity.valueOf(getCallbackValue(callbackQuery.getData()));
        log.info("Callback with type :{} and activity : {}",MY_OFFERS,activity);
        requestService.checkAndClearChangingOfferRequests(callbackQuery.getMessage().getChatId());
        var offerList = requestService.findByUserIdAndActivity(callbackQuery.getFrom().getId(),activity);
        return !offerList.isEmpty() ?
                makeSendMessage(offerList, callbackQuery) :
                EditMessageText.builder()
                        .chatId(callbackQuery.getMessage().getChatId().toString())
                        .messageId(callbackQuery.getMessage().getMessageId())
                        .text("You have no offers yet")
                        .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboard(List.of(List.of(InlineKeyboardButton
                                        .builder()
                                        .text("Back to menu")
                                        .callbackData(MAIN_MENU.name())
                                        .build())))
                                .build())
                        .build();
    }

    private EditMessageText makeSendMessage(List<ChangeDriverRequest> requests, CallbackQuery callbackQuery) {
        var buttons = requests.stream()
                .map(request -> List.of(InlineKeyboardButton.builder()
                        .text(String.format(OFFERS_OF_DRIVERS_PATTERN, request.getCities().stream().map(City::getName)
                                .collect(Collectors.joining("➖"))))
                        .callbackData(CHANGE_OFFER.name() + SPLITTER +request.getOfferId())
                        .build()))
                .collect(Collectors.toList());
        buttons.add(List.of(InlineKeyboardButton
                .builder()
                .text("Back to menu")
                .callbackData(Callbacks.MAIN_MENU.name())
                .build()));
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text("Here is yours offers. If you want to change one of them, just press on the offer you are interested in ")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

}
