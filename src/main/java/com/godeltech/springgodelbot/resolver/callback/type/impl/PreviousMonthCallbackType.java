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

import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.createCalendar;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_FIRST_DATE;
import static com.godeltech.springgodelbot.util.ConstantUtil.CHOOSE_THE_SECOND_DATE;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreviousMonthCallbackType implements CallbackType {
    private final RequestService requestService;
    @Override
    public String getCallbackName() {
        return Callbacks.PREVIOUS_MONTH.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        log.info("Got Next Month Callback type with callback :{} by user: {}", callbackQuery.getData(),
                callbackQuery.getFrom().getUserName());
        Callbacks callback = Callbacks.valueOf(getCallbackValue(callbackQuery.getData()));
        LocalDate localDate = LocalDate.parse(callbackQuery.getData().split(SPLITTER)[2]).minusMonths(1);
        var chosenDate = returnChosenDate(callbackQuery,callback);
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(returnText(callback,localDate,chosenDate))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(chosenDate==null?
                                createCalendar(localDate, callback.name()):
                                createCalendar(localDate,callback.name(),chosenDate,YES))
                        .build())
                .build();

    }
    private LocalDate returnChosenDate(CallbackQuery callbackQuery, Callbacks callback) {
        switch (callback) {
            case SECOND_DATE_DRIVER:
                return requestService.getDriverRequest(callbackQuery.getMessage())
                        .getFirstDate();
            case SECOND_DATE_PASSENGER:
                return requestService.getPassengerRequest(callbackQuery.getMessage())
                        .getFirstDate();
            default:
                return null;
        }
    }
    private String returnText(Callbacks callback, LocalDate localDate, LocalDate chosenDate) {
        switch (callback) {
            case SECOND_DATE_DRIVER:
            case SECOND_DATE_PASSENGER:
            case CHANGE_SECOND_DATE_OF_OFFER:
                return String.format(CHOOSE_THE_SECOND_DATE, chosenDate, localDate.getMonth(), localDate.getYear());
            case FIRST_DATE_DRIVER:
            case FIRST_DATE_PASSENGER:
            case CHANGE_FIRST_DATE_OF_OFFER:
                return String.format(CHOOSE_THE_FIRST_DATE, localDate.getMonth(), localDate.getYear());
            default:
                throw new UnknownCommandException();
        }
    }
}
