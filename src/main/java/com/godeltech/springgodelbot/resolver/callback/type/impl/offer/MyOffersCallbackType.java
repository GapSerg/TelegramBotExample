package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
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
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.OFFERS_OF_DRIVERS_PATTERN;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyOffersCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.MY_OFFERS.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        Activity activity = Activity.valueOf(getCallbackValue(callbackQuery.getData()));
        log.info("Callback with type :{} and activity : {} and token: {}", MY_OFFERS, activity,token);
        requestService.checkAndClearChangingOfferRequests(token);
        List<ChangeOfferRequest> offerList = requestService
                .findByUserIdAndActivity(callbackQuery.getFrom().getId(), activity);
        return offerList.isEmpty() ?
                EditMessageText.builder()
                        .chatId(callbackQuery.getMessage().getChatId().toString())
                        .messageId(callbackQuery.getMessage().getMessageId())
                        .text("You have no offers yet")
                        .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboard(List.of(List.of(InlineKeyboardButton
                                        .builder()
                                        .text("Back to menu")
                                        .callbackData(MAIN_MENU.ordinal()+SPLITTER+token)
                                        .build())))
                                .build())
                        .build():
                makeSendMessage(offerList, callbackQuery,token) ;
    }

    private EditMessageText makeSendMessage(List<ChangeOfferRequest> requests, CallbackQuery callbackQuery,String token) {
        List<List<InlineKeyboardButton>> buttons = requests.stream()
                .map(request -> List.of(InlineKeyboardButton.builder()
                        .text(String.format(OFFERS_OF_DRIVERS_PATTERN, request.getCities().stream().map(City::getName)
                                .collect(Collectors.joining("➖"))))
                        .callbackData(CHANGE_OFFER.ordinal() +SPLITTER+token+ SPLITTER + request.getOfferId())
                        .build()))
                .collect(Collectors.toList());
        buttons.add(List.of(InlineKeyboardButton
                .builder()
                .text("Back to menu")
                .callbackData(CANCEL_CHANGE_OFFER_REQUEST.ordinal()+SPLITTER+token)
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
