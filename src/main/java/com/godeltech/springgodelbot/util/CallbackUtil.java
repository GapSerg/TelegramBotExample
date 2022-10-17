package com.godeltech.springgodelbot.util;

import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.DateUtil.getDatesInf;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRouteFromCities;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

public class CallbackUtil {

    public static class RouteUtil {

        public static EditMessageText createRouteEditMessageText(List<City> cities, Integer callback, Integer cancelRequestCallback,
                                                                 Message message, String token, String messageText) {
            List<List<InlineKeyboardButton>> buttons = getButtonsList();
            for (int i = 0; i < cities.size(); ) {
                i = addCitiesToButtons(cities, callback, token, buttons, i);
            }
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token, "Back to menu")));
            return getEditTextMessageForRoute(message, buttons, messageText);
        }


        public static BotApiMethod createEditSendMessageForRoutes(CallbackQuery callbackQuery,
                                                                  List<City> cities,
                                                                  List<String> reservedCities,
                                                                  Integer callback, Integer cancelRouteCallback,
                                                                  Integer cancelRequestCallback, String token, String textMessage) {
            List<List<InlineKeyboardButton>> buttons = getRouteButtons(cities, reservedCities, callback, cancelRouteCallback, cancelRequestCallback, token);
            return EditMessageText.builder()
                    .text(textMessage)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .build();
        }

        private static List<List<InlineKeyboardButton>> getRouteButtons(List<City> cities, List<String> reservedCities, Integer callback, Integer cancelRouteCallback, Integer cancelRequestCallback, String token) {
            List<List<InlineKeyboardButton>> buttons = getButtonsList();
            for (int i = 0; i < cities.size(); ) {
                i = addCitiesToButtons(cities, reservedCities, callback, cancelRouteCallback, token, buttons, i);
            }
            if (reservedCities.size() >= 2) {
                buttons.add(List.of(getCancelButton(cancelRequestCallback, token, getCancelText(cancelRequestCallback)),
                        InlineKeyboardButton.builder()
                                .text(NEXT)
                                .callbackData(getChoseDateCallback(callback, token))
                                .build()));
            } else {
                buttons.add(List.of(getCancelButton(cancelRequestCallback, token, getCancelText(cancelRequestCallback))));
            }
            return buttons;
        }

        private static int addCitiesToButtons(List<City> cities, Integer callback, String token, List<List<InlineKeyboardButton>> buttons, int i) {
            if (i % 2 == 0) {
                buttons.add(List.of(makeUnmarkedRouteButton(cities.get(i++), callback, token),
                        makeUnmarkedRouteButton(cities.get(i++), callback, token)));
            } else {
                buttons.add(List.of(makeUnmarkedRouteButton(cities.get(i++), callback, token)));
            }
            return i;
        }

        private static int addCitiesToButtons(List<City> cities,
                                              List<String> reservedCities,
                                              Integer routeCallback, Integer cancelRouteCallback,
                                              String token,
                                              List<List<InlineKeyboardButton>> buttons, int i) {
            if (i % 2 == 0) {
                buttons.add(List.of(makeSuitableRouteButton(cities.get(i++), reservedCities, routeCallback, cancelRouteCallback, token),
                        makeSuitableRouteButton(cities.get(i++), reservedCities, routeCallback, cancelRouteCallback, token)));
            } else {
                buttons.add(List.of(makeSuitableRouteButton(cities.get(i++), reservedCities, routeCallback, cancelRouteCallback, token)));
            }
            return i;
        }

        private static InlineKeyboardButton makeSuitableRouteButton(City city, List<String> reservedCities,
                                                                    Integer routeCallback, Integer cancelRouteCallback,
                                                                    String token) {
            return reservedCities.contains(city.getName()) ?
                    makeMarkedRouteButton(city, cancelRouteCallback, token, reservedCities.lastIndexOf(city.getName())) :
                    makeUnmarkedRouteButton(city, routeCallback, token);
        }

        public static String getCurrentRoute(List<String> reservedCities) {
            return String.join("➡", reservedCities);
        }

        private static InlineKeyboardButton makeUnmarkedRouteButton(City city, Integer callback, String token) {
            return InlineKeyboardButton.builder()
                    .text(city.getName())
                    .callbackData(callback + SPLITTER + token + SPLITTER + city.getId())
                    .build();
        }

        private static InlineKeyboardButton makeMarkedRouteButton(City city, Integer cancelCallback, String token, int index) {
            return InlineKeyboardButton.builder()
                    .text(getStartPhrase(index) + city.getName() + CORRECT_MARKER)
                    .callbackData(cancelCallback + SPLITTER + token + SPLITTER + city.getId())
                    .build();
        }

        public static String getCurrentRoute(List<String> reservedCities, Activity activity) {
            String textMessage = String.join("➡", reservedCities);
            return activity.equals(Activity.DRIVER) ?
                    textMessage + " \uD83D\uDE99" :
                    textMessage + " \uD83D\uDCBA";
        }

        public static String getCurrentRouteFromCities(List<City> reservedCities, Activity activity) {
            String textMessage = reservedCities
                    .stream()
                    .map(City::getName)
                    .collect(Collectors.joining("➡"));
            return activity.equals(Activity.DRIVER) ?
                    textMessage + " \uD83D\uDE99" :
                    textMessage + " \uD83D\uDCBA";

        }

        private static String getChoseDateCallback(Integer callback, String token) {
            switch (Callbacks.values()[callback]) {
                case DRIVER_ROUTE:
                    return CHOSE_DATE_DRIVER.ordinal() + SPLITTER + token;
                case PASSENGER_ROUTE:
                    return CHOSE_DATE_PASSENGER.ordinal() + SPLITTER + token;
                case CHANGE_ROUTE_OF_OFFER:
                    return FINISH_CHANGING_ROUTE_OF_OFFER.ordinal() + SPLITTER + token;
                default:
                    throw new UnknownCommandException();
            }
        }


        private static String getStartPhrase(int index) {
            return index > 0 ?
                    "TO:  " :
                    "FROM: ";
        }
    }


    public static class DateUtil {
        public static EditMessageText createEditMessageForSecondDate(CallbackQuery callbackQuery, LocalDate firstDate,
                                                                     String text, Integer callback, Integer cancelRequestCallback, String token) {
            List<List<InlineKeyboardButton>> buttons = createCalendar(firstDate, callback, cancelRequestCallback,
                    firstDate, CORRECT_MARKER, token);
            return EditMessageText.builder()
                    .text(text)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageForSecondDate(CallbackQuery callbackQuery, LocalDate firstDate,
                                                                     String textMessage, Integer callback, Integer cancelRequestCallback,
                                                                     LocalDate secondDate, String token) {
            LocalDate date = LocalDate.now();
            List<List<InlineKeyboardButton>> buttons = createCalendar(date, callback, cancelRequestCallback,
                    firstDate, CORRECT_MARKER, secondDate, CORRECT_MARKER, token);
            return EditMessageText.builder()
                    .text(textMessage)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .build();
        }

        public static EditMessageText createSendMessageForFirstDate(Message message, Integer callback, Integer cancelRequestCallback,
                                                                    String textMessage, String token) {
            LocalDate date = LocalDate.now();
            List<List<InlineKeyboardButton>> buttons = createCalendar(date, callback, cancelRequestCallback, token);
            return EditMessageText.builder()
                    .text(textMessage)
                    .chatId(message.getChatId().toString())
                    .messageId(message.getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageForFirstDate(CallbackQuery callbackQuery, Integer callback,
                                                                    Integer cancelRequestCallback, String textMessage, String token) {
            LocalDate date = LocalDate.now();
            return EditMessageText.builder()
                    .text(textMessage)
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(date, callback, cancelRequestCallback, token))
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageTextForFirstDate(CallbackQuery callbackQuery, Integer callback,
                                                                        Integer cancelRequestCallback,
                                                                        String textMessage, LocalDate changedDate, String token) {

            return EditMessageText.builder()
                    .text(textMessage)
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(changedDate, callback, cancelRequestCallback, token))
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageTextForFirstDateWithIncorrectDate(CallbackQuery callbackQuery,
                                                                                         Integer callback, Integer cancelRequestCallback,
                                                                                         String text,
                                                                                         LocalDate incorrectDate, String token) {
            LocalDate date = LocalDate.now();
            return EditMessageText.builder()
                    .text(String.format(text, date.getMonth(), date.getYear()))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(date, callback, cancelRequestCallback, incorrectDate, INCORRECT_MARKER, token))
                            .build())
                    .build();
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, Integer callback, Integer cancelRequestCallback, String token) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtons(callback, numberDayInMonth, date, token))
                    .collect(Collectors.toList());
//            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons, token);
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token, getCancelText(cancelRequestCallback))));
            return buttons;
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, Integer callback, Integer cancelRequestCallback,
                                                                      LocalDate chosenDate, String mark, String token) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtonsWithReservedDate(callback, numberDayInMonth, date, chosenDate, mark, token))
                    .collect(Collectors.toList());
//            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons, token);
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token, getCancelText(cancelRequestCallback)),
                    getFinishDateButton(callback, token)));
            return buttons;
        }

        private static InlineKeyboardButton getFinishDateButton(Integer callback, String token) {
            return InlineKeyboardButton.builder()
                    .text(NEXT)
                    .callbackData(getFinishCallback(callback) + SPLITTER + token)
                    .build();
        }

        private static int getFinishCallback(Integer callback) {
            switch (Callbacks.values()[callback]) {
                case FIRST_DATE_DRIVER:
                case SECOND_DATE_DRIVER:
                    return FINISH_CHOSE_DATE_DRIVER.ordinal();
                case FIRST_DATE_PASSENGER:
                case SECOND_DATE_PASSENGER:
                    return FINISH_CHOSE_DATE_PASSENGER.ordinal();
                case CHANGE_FIRST_DATE_OF_OFFER:
                case CHANGE_SECOND_DATE_OF_OFFER:
                    return FINISH_DATE_OFFER.ordinal();
                default:
                    throw new UnknownCommandException();
            }
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, Integer callback,
                                                                      Integer cancelRequestCallback,
                                                                      LocalDate chosenDate, String mark,
                                                                      LocalDate invalidDate, String invalidMark, String token) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtonsWithReservedDate(callback, numberDayInMonth, date, chosenDate, mark,
                            invalidDate, invalidMark, token))
                    .collect(Collectors.toList());
//            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons, token);
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token, getCancelText(cancelRequestCallback)),
                    getFinishDateButton(callback, token)));
            return buttons;
        }

        private static List<LocalDate> createListOfDateWithPeriod(LocalDate localDate, Integer numberDaysInMonth) {
//            List<LocalDate> list = localDate.datesUntil(localDate.withDayOfMonth(localDate.getMonth().length(localDate.isLeapYear())), Period.ofDays(3))
//                    .collect(Collectors.toList());
            LocalDate currentDate = LocalDate.now();
            List<LocalDate> list = currentDate.datesUntil(currentDate.plusDays(30), Period.ofDays(3))
                    .collect(Collectors.toList());
//            if (numberDaysInMonth - list.get(list.size() - 1).getDayOfMonth() == 3)
//                list.add(localDate.withDayOfMonth(numberDaysInMonth));
            return list;
        }

        private static List<InlineKeyboardButton> addRowOfButtons(Integer callback, int numberDaysInMonth, LocalDate date, String token) {
            return getInlineKeyboardButtons(callback, numberDaysInMonth, date, token);
        }

        private static List<InlineKeyboardButton> addRowOfButtonsWithReservedDate(Integer callback, int numberDaysInMonth,
                                                                                  LocalDate date, LocalDate chosenDate, String mark, String token) {
            LocalDate plusDate = chosenDate.plusDays(2);
            return chosenDate.getMonth().equals(date.getMonth()) || chosenDate.getMonth().equals(plusDate.getMonth()) ?
                    getInlineKeyboardButtonsTheSameMonth(callback, numberDaysInMonth, date, chosenDate, mark, token)
                    : getInlineKeyboardButtons(callback, numberDaysInMonth, date, token);
        }

        private static List<InlineKeyboardButton> addRowOfButtonsWithReservedDate(Integer callback, int numberDaysInMonth,
                                                                                  LocalDate date, LocalDate chosenDate, String mark,
                                                                                  LocalDate invalidDate, String invalidMark, String token) {
            LocalDate plusDate = date.plusDays(2);
            return (chosenDate.getMonth().equals(date.getMonth()) || invalidDate.getMonth().equals(date.getMonth())
                    || chosenDate.getMonth().equals(plusDate.getMonth()) || invalidDate.getMonth().equals(plusDate.getMonth())) ?
                    getInlineKeyboardButtonsTheSameMonth(callback, numberDaysInMonth, date, chosenDate, mark, invalidDate, invalidMark, token)
                    : getInlineKeyboardButtons(callback, numberDaysInMonth, date, token);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtons(Integer callback, int numberDaysInMonth, LocalDate date, String token) {
//            int i= 0;
//            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
            return createDateRowWithThreeDays(callback, date, token);
//            }
//            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
//                return createDateRowWithTwoDays(callback, date, token);
//            }
//            return createDateRowWithOneDay(callback, date, token);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtonsTheSameMonth(Integer callback, int numberDaysInMonth,
                                                                                       LocalDate date,
                                                                                       LocalDate chosenDate, String mark, String token) {
//            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
            return createDateRowWithThreeDays(callback, date, chosenDate, mark, token);
//            }
//            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
//                return createDateRowWithTwoDays(callback, date, chosenDate, mark, token);
//            }
//            return createDateRowWithOneDay(callback, date, chosenDate, mark, token);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtonsTheSameMonth(Integer callback, int numberDaysInMonth,
                                                                                       LocalDate date,
                                                                                       LocalDate chosenDate, String mark,
                                                                                       LocalDate invalidDate, String invalidMark, String token) {
//            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
            return createDateRowWithThreeDays(callback, date, chosenDate, mark, invalidDate, invalidMark, token);
//            }
//            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
//                return createDateRowWithTwoDays(callback, date, chosenDate, mark, invalidDate, invalidMark, token);
//            }
//            return createDateRowWithOneDay(callback, date, chosenDate, mark, invalidDate, invalidMark, token);
        }

        private static List<InlineKeyboardButton> createDateRowWithOneDay(Integer callback, LocalDate date, String token) {
            return List.of(createDateButton(date, callback, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithOneDay(Integer callback, LocalDate date,
                                                                          LocalDate chosenDate, String mark, String token) {
            return List.of(createDateButton(date, chosenDate, callback, mark, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithOneDay(Integer callback, LocalDate date,
                                                                          LocalDate chosenDate, String mark,
                                                                          LocalDate invalidDate, String invalidMark, String token) {
            return List.of(createDateButton(date, callback, chosenDate, mark, invalidDate, invalidMark, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithTwoDays(Integer callback, LocalDate date, String token) {
            return List.of(createDateButton(date, callback, token),
                    createDateButton(date.plusDays(1), callback, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithTwoDays(Integer callback, LocalDate date,
                                                                           LocalDate chosenDate, String mark, String token) {
            return List.of(createDateButton(date, chosenDate, callback, mark, token),
                    createDateButton(date.plusDays(1), chosenDate, callback, mark, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithTwoDays(Integer callback, LocalDate date,
                                                                           LocalDate chosenDate, String mark,
                                                                           LocalDate invalidDate, String invalidMark, String token) {
            return List.of(createDateButton(date, callback, chosenDate, mark, invalidDate, invalidMark, token),
                    createDateButton(date.plusDays(1), callback, chosenDate, mark, invalidDate, invalidMark, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithThreeDays(Integer callback, LocalDate date, String token) {
            return List.of(createDateButton(date, callback, token),
                    createDateButton(date.plusDays(1), callback, token),
                    createDateButton(date.plusDays(2), callback, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithThreeDays(Integer callback, LocalDate date,
                                                                             LocalDate chosenDate, String mark, String token) {
            return List.of(createDateButton(date, chosenDate, callback, mark, token),
                    createDateButton(date.plusDays(1), chosenDate, callback, mark, token),
                    createDateButton(date.plusDays(2), chosenDate, callback, mark, token));
        }

        private static List<InlineKeyboardButton> createDateRowWithThreeDays(Integer callback, LocalDate date,
                                                                             LocalDate chosenDate, String mark,
                                                                             LocalDate invalidDate, String invalidMark, String token) {
            return List.of(createDateButton(date, callback, chosenDate, mark, invalidDate, invalidMark, token),
                    createDateButton(date.plusDays(1), callback, chosenDate, mark, invalidDate, invalidMark, token),
                    createDateButton(date.plusDays(2), callback, chosenDate, mark, invalidDate, invalidMark, token));
        }

        private static void addLinksOnPreviousAndNextMonths(LocalDate localDate, Integer callback, List<List<InlineKeyboardButton>> buttons, String token) {
            if (!localDate.getMonth().equals(LocalDate.now().getMonth())) {
                buttons.add(List.of(
                        createMonthButton(localDate, PREVIOUS_MONTH.ordinal() + SPLITTER + token + SPLITTER + callback, "Previous Month"),
                        createMonthButton(localDate, NEXT_MONTH.ordinal() + SPLITTER + token + SPLITTER + callback, "Next Month")));
            } else {
                buttons.add(List.of(
                        createMonthButton(localDate, NEXT_MONTH.ordinal() + SPLITTER + token + SPLITTER + callback, "Next Month"))
                );
            }
        }


        private static Integer getCancelCallback(Integer callback) {
            switch (Callbacks.values()[callback]) {
                case FIRST_DATE_DRIVER:
                case SECOND_DATE_DRIVER:
                    return CANCEL_DATE_DRIVER.ordinal();
                case FIRST_DATE_PASSENGER:
                case SECOND_DATE_PASSENGER:
                    return CANCEL_DATE_PASSENGER.ordinal();
                case CHANGE_FIRST_DATE_OF_OFFER:
                case CHANGE_SECOND_DATE_OF_OFFER:
                    return CANCEL_DATE_OF_OFFER.ordinal();
                default:
                    throw new UnknownCommandException();
            }
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, Integer callback, String token) {
            return InlineKeyboardButton.builder()
                    .text(String.format(DATE_FORMAT, localDate.getDayOfMonth(), localDate.getMonth().toString().substring(0, 3)))
                    .callbackData(callback + SPLITTER + token + SPLITTER + localDate)
                    .build();
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, LocalDate chosenDate, Integer callback, String mark, String token) {
            return chosenDate.equals(localDate) ? InlineKeyboardButton.builder()
                    .text(String.format(DATE_FORMAT, localDate.getDayOfMonth(), localDate.getMonth().toString().substring(0, 3)) + mark)
                    .callbackData(getCancelCallback(callback) + SPLITTER + token + SPLITTER + localDate)
                    .build() :
                    InlineKeyboardButton.builder()
                            .text(String.format(DATE_FORMAT, localDate.getDayOfMonth(), localDate.getMonth().toString().substring(0, 3)))
                            .callbackData(callback + SPLITTER + token + SPLITTER + localDate)
                            .build();
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, Integer callback, LocalDate chosenDate, String mark,
                 LocalDate invalidDate, String invalidMark, String token) {
            if (chosenDate.equals(localDate) || invalidDate.equals(localDate)) {
                return InlineKeyboardButton.builder()
                        .text(String.format(DATE_FORMAT, localDate.getDayOfMonth(), localDate.getMonth().toString().substring(0, 3)) + mark)
                        .callbackData(getCancelCallback(callback) + SPLITTER + token + SPLITTER + localDate)
                        .build();
            } else {
                return InlineKeyboardButton.builder()
                        .text(String.format(DATE_FORMAT, localDate.getDayOfMonth(), localDate.getMonth().toString().substring(0, 3)))
                        .callbackData(callback + SPLITTER + token + SPLITTER + localDate)
                        .build();
            }
        }

        private static InlineKeyboardButton createMonthButton(LocalDate localDate, String callback, String text) {
            return InlineKeyboardButton.builder()
                    .text(text)
                    .callbackData(callback + SPLITTER + localDate)
                    .build();
        }

        public static void setDatesToRequest(LocalDate chosenDate, Request request) {
            if (request.getSecondDate() == null) {
                if (request.getFirstDate().isAfter(chosenDate)) {
                    request.setSecondDate(request.getFirstDate());
                    request.setFirstDate(chosenDate);
                } else {
                    request.setSecondDate(chosenDate);
                }
            } else {
                if (chosenDate.isAfter(request.getFirstDate())) {
                    request.setSecondDate(chosenDate);
                } else {
                    request.setFirstDate(chosenDate);
                }
            }
        }

        public static String getDatesInf(LocalDate firstDate, LocalDate secondDate) {
            if (firstDate == null) {
                return NO_CHOSEN_DATE;
            } else if (secondDate == null) {
                return String.format(CHOSEN_DATE, firstDate);
            } else {
                return String.format(CHOSEN_DATES, firstDate, secondDate);
            }
        }
    }

    private static List<List<InlineKeyboardButton>> getButtonsList() {
        return new ArrayList<>();
    }

    public static String getCallbackValue(String dataCallback) {
        return dataCallback.split(SPLITTER)[2];
    }

    public static String getCallbackToken(String dataCallback) {
        return dataCallback.split(SPLITTER)[1];
    }

    private static EditMessageText getEditTextMessageForRoute(Message message, List<List<InlineKeyboardButton>> buttons, String messageText) {
        return EditMessageText.builder()
                .text(messageText)
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    public static EditMessageText createEditMessageTextAfterConfirm(CallbackQuery callbackQuery, Integer callback, String message, String token) {
        List<List<InlineKeyboardButton>> buttons = List.of(List.of(
                getCancelButton(callback, token, SAVE_WITHOUT_DESCRIPTION)));
        return EditMessageText.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .text(message)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    public static EditMessageText createSendMessageWithDoubleCheckOffer(CallbackQuery callbackQuery,
                                                                        String textMessage,
                                                                        Integer checkCallback,
                                                                        Integer cancelCallback, String token) {

        List<List<InlineKeyboardButton>> buttons = List.of(List.of(
                getCancelButton(checkCallback, token, SAVE),
                cancelRequest(cancelCallback, token)));

        return getEditTextMessageForRoute(callbackQuery.getMessage(), buttons,
                String.format(ASK_FOR_DESIRE_TO_SAVE, textMessage));
    }

    private static InlineKeyboardButton cancelRequest(Integer cancelCallback, String token) {
        return getCancelButton(cancelCallback, token, MENU);
    }

    public static SendMessage makeSendMessageForUserWithoutUsername(Message message) {
        return SendMessage.builder()
                .text(HAVE_NO_USERNAME)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                .text(USERNAME_IS_ADDED)
                                .callbackData(String.valueOf(MAIN_MENU.ordinal()))
                                .build())).build())
                .build();
    }

    public static EditMessageText makeEditMessageForUserWithoutUsername(Message message) {
        return EditMessageText.builder()
                .text(HAVE_NO_USERNAME)
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                .text(USERNAME_IS_ADDED)
                                .callbackData(String.valueOf(MAIN_MENU.ordinal()))
                                .build())).build())
                .build();
    }

    public static String getListOfOffersForRequest(List<Offer> offers) {
        return offers.stream()
                .map(CallbackUtil::getOffersViewForRequest)
                .collect(Collectors.joining("\n\n"));
    }

    public static String getOffersView(Request request) {
        return request.getDescription() != null ?
                String.format(OFFER_OF_CHANGING_OFFER_PATTERN, request.getActivity(),
                        getCurrentRoute(request.getCities(), request.getActivity()), getDatesInf(request.getFirstDate(), request.getSecondDate()),
                        request.getDescription()) :
                String.format(OFFER_OF_CHANGING_OFFER_PATTERN_WITHOUT_DESC, request.getActivity(),
                        getCurrentRoute(request.getCities(), request.getActivity()), getDatesInf(request.getFirstDate(),
                                request.getSecondDate()));
    }

    public static String getOffersViewForRequest(Offer offer) {
        return offer.getDescription() != null ?
                String.format(OFFERS_FOR_REQUESTS_PATTERN, getCorrectName(offer.getUserEntity().getFirstName()),
                        getCorrectName(offer.getUserEntity().getLastName()), offer.getActivity(), getCurrentRouteFromCities(offer.getCities(), offer.getActivity()),
                        getDatesInf(offer.getFirstDate(), offer.getSecondDate()),
                        offer.getDescription(), offer.getUserEntity().getUserName()) :
                String.format(OFFERS_FOR_REQUESTS_PATTERN_WITHOUT_DESC, getCorrectName(offer.getUserEntity().getFirstName()),
                        getCorrectName(offer.getUserEntity().getLastName()), offer.getActivity(), getCurrentRouteFromCities(offer.getCities(), offer.getActivity()),
                        getDatesInf(offer.getFirstDate(), offer.getSecondDate()), offer.getUserEntity().getUserName());
    }

    private static String getCorrectName(String name) {
        return name == null ?
                EMPTY :
                name;
    }

    public static EditMessageText getAvailableOffersList(List<Offer> requests, CallbackQuery callbackQuery, String message, String token) {
        return EditMessageText.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .text(message)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(getCancelButton(MAIN_MENU.ordinal(), token, "Back to main menu"))))
                        .build())
                .build();
    }

    public static EditMessageText getEditTextMessageForOffer(CallbackQuery callbackQuery, String token, Request request, String messageText) {
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(messageText)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(getChangeOfferButtons(request, token))
                        .build())
                .build();
    }

    private static List<List<InlineKeyboardButton>> getChangeOfferButtons(Request request, String token) {
        return List.of(
                List.of(InlineKeyboardButton.builder()
                        .text("Suitable offers")
                        .callbackData(SHOW_SUITABLE_OFFERS.ordinal() + SPLITTER + token)
                        .build()),
                List.of(InlineKeyboardButton.builder()
                                .text("Change route")
                                .callbackData(CHANGE_ROUTE_OF_OFFER.ordinal() + SPLITTER + token)
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Change date")
                                .callbackData(CHANGE_DATE_OF_OFFER.ordinal() + SPLITTER + token)
                                .build()
                ),
                List.of(InlineKeyboardButton.builder()
                        .text("Change description")
                        .callbackData(CHANGE_DESCRIPTION_OF_OFFER.ordinal() + SPLITTER + token)
                        .build(), InlineKeyboardButton.builder()
                        .text("Delete offer")
                        .callbackData(DELETE_OFFER.ordinal() + SPLITTER + token + SPLITTER + request.getOfferId())
                        .build()),
                List.of(InlineKeyboardButton.builder()
                        .text("Back to offer list")
                        .callbackData(MY_OFFERS.ordinal() + SPLITTER + token + SPLITTER + request.getActivity())
                        .build()));
    }

    public static String getCompletedMessageAnswer(List<Offer> offers, Request request, String completedMessage) {
        return offers.isEmpty() ? String.format(NO_SUITABLE_OFFERS, completedMessage,
                request.getActivity(),
                getCurrentRoute(request.getCities()),
                getDatesInf(request.getFirstDate(), request.getSecondDate()), descriptionInf(request.getDescription())) :
                String.format(SUITABLE_OFFERS, completedMessage, request.getActivity(),
                        getCurrentRoute(request.getCities()),
                        getDatesInf(request.getFirstDate(), request.getSecondDate()),
                        descriptionInf(request.getDescription()),
                        getListOfOffersForRequest(offers));
    }

    private static String descriptionInf(String description) {
        return description == null ?
                "" :
                "Description: " + description;
    }

    public static SendMessage showSavedRequestWithDescription(Message message, Request request, List<Offer> offers,
                                                              Callbacks callback, String messageText) {
        return SendMessage.builder()
                .text(getCompletedMessageAnswer(offers, request, messageText))
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(
                                InlineKeyboardButton.builder()
                                        .text("Main menu")
                                        .callbackData(callback.ordinal() + SPLITTER + request.getToken().getId())
                                        .build()
                        )))
                        .build())
                .build();
    }

    public static EditMessageText showSavedRequestWithoutDescription(CallbackQuery callbackQuery, Request request,
                                                                     Callbacks callback,
                                                                     List<Offer> offers, String messageText) {
        return EditMessageText.builder()
                .text(getCompletedMessageAnswer(offers, request, messageText))
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(
                                InlineKeyboardButton.builder()
                                        .text("Main menu")
                                        .callbackData(callback.ordinal() + SPLITTER + request.getToken().getId())
                                        .build()
                        )))
                        .build())
                .build();
    }

    private static String getCancelText(Integer cancelRequestCallback) {
        return cancelRequestCallback.equals(RETURN_TO_CHANGE_OF_OFFER.ordinal()) ?
                "Back" : "Back to menu";
    }

    public static InlineKeyboardButton getCancelButton(Integer cancelRequestCallback, String token, String message) {
        return InlineKeyboardButton.builder()
                .text(message)
                .callbackData(cancelRequestCallback + SPLITTER + token)
                .build();
    }

}
