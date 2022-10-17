package com.godeltech.springgodelbot.resolver.callback.type.impl;

import com.godeltech.springgodelbot.exception.UnknownCommandException;
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

import java.time.LocalDate;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createCalendar;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreviousMonthCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.PREVIOUS_MONTH.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got Next Month Callback type with callback :{} by user: {}", callbackQuery.getData(),
                callbackQuery.getFrom().getUserName());
        String token = getCallbackToken(callbackQuery.getData());
        Callbacks callback = Callbacks.values()[Integer.parseInt(getCallbackValue(callbackQuery.getData()))];
        LocalDate localDate = LocalDate.parse(callbackQuery.getData().split(SPLITTER)[3]).minusMonths(1);
        LocalDate chosenDate = returnChosenDate(callbackQuery, callback, token);
        Callbacks cancelCallback = getCancelCallback(callback);
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(returnText(callback, localDate, chosenDate))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(chosenDate == null ?
                                createCalendar(localDate, callback.ordinal(), cancelCallback.ordinal(), token) :
                                createCalendar(localDate, callback.ordinal(), cancelCallback.ordinal(), chosenDate,
                                        CORRECT_MARKER, token))
                        .build())
                .build();

    }

    private LocalDate returnChosenDate(CallbackQuery callbackQuery, Callbacks callback, String token) {
                return requestService.getRequest(callbackQuery.getMessage(), token,callbackQuery.getFrom() )
                        .getFirstDate();
    }

    private String returnText(Callbacks callback, LocalDate localDate, LocalDate chosenDate) {
        switch (callback) {
            case SECOND_DATE_DRIVER:
            case SECOND_DATE_PASSENGER:
            case CHANGE_SECOND_DATE_OF_OFFER:
                return String.format(CHOSEN_FIRST_DATE, chosenDate, localDate.getMonth(), localDate.getYear());
            case FIRST_DATE_DRIVER:
            case FIRST_DATE_PASSENGER:
            case CHANGE_FIRST_DATE_OF_OFFER:
                return String.format(CHOOSE_THE_FIRST_DATE, localDate.getMonth(), localDate.getYear());
            default:
                throw new UnknownCommandException();
        }
    }

    private Callbacks getCancelCallback(Callbacks callback) {
        switch (callback) {
            case FIRST_DATE_DRIVER:
            case SECOND_DATE_DRIVER:
                return CANCEL_DRIVER_REQUEST;
            case FIRST_DATE_PASSENGER:
            case SECOND_DATE_PASSENGER:
                return CANCEL_PASSENGER_REQUEST;
            case CHANGE_FIRST_DATE_OF_OFFER:
            case CHANGE_SECOND_DATE_OF_OFFER:
                return RETURN_TO_CHANGE_OF_OFFER;
        }
        throw new UnknownCommandException();
    }
}
