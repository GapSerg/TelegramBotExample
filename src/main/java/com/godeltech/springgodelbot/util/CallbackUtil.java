package com.godeltech.springgodelbot.util;

import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.City;
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
import static com.godeltech.springgodelbot.util.CallbackUtil.RouteUtil.getCurrentRoute;
import static com.godeltech.springgodelbot.util.ConstantUtil.*;

public class CallbackUtil {

    public static final String SPLITTER = "&";
    public static final String KRESTIK = "❌";
    public static final String YES = "✅";
    public static final String MENU = "MENU";
    public static final String HAVE_NO_USERNAME = "You don't have a username, please add it in your personal settings.When you deal with it,  just press the button";
    public static final String USERNAME_IS_ADDED = "I've added my username";
    public static final String EMPTY = " ";


    public static class RouteUtil {

        public static final String MARKER = "✅";


        public static SendMessage createRouteSendMessage(List<City> cities, Callbacks callback, Long chatId) {
            List<List<InlineKeyboardButton>> buttons = getButtonsList();
            cities.forEach(route -> buttons.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(route.getName())
                            .callbackData(callback + SPLITTER + route.getId())
                            .build()
            )));
            return getSendMessage(chatId, buttons, CHOSE_THE_ROUTE);
        }


        public static BotApiMethod createEditSendMessageForRoutes(CallbackQuery callbackQuery,
                                                                  List<City> cities,
                                                                  List<City> reservedCities,
                                                                  Callbacks callback, Callbacks cancelCallback) {
            List<List<InlineKeyboardButton>> buttons = cities.stream()
                    .map(route -> reservedCities.contains(route) ?
                            makeMarkedRouteButton(route, cancelCallback) :
                            makeUnmarkedRouteButton(route, callback))
                    .collect(Collectors.toList());
            if (reservedCities.size() >= 2)
                buttons.add(List.of(InlineKeyboardButton.builder()
                        .text(FINISH)
                        .callbackData(getChoseDateCallback(callback))
                        .build()));
            return EditMessageText.builder()
                    .text(reservedCities.isEmpty() ? CHOSE_THE_ROUTE :
                            String.format(CURRENT_ROUTE, getCurrentRoute(reservedCities)))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .build();
        }

        public static String getCurrentRoute(List<City> reservedCities) {
            return reservedCities
                    .stream()
                    .map(City::getName)
                    .collect(Collectors.joining("➡"));
        }

        private static String getChoseDateCallback(Callbacks callback) {
            switch (callback) {
                case DRIVER_ROUTE:
                    return CHOSE_DATE_DRIVER.name();
                case PASSENGER_ROUTE:
                    return CHOSE_DATE_PASSENGER.name();
                case CHANGE_ROUTE_OF_OFFER:
                    return FINISH_CHANGING_ROUTE_OF_OFFER.name();
                default:
                    throw new UnknownCommandException();
            }
        }

        private static List<InlineKeyboardButton> makeUnmarkedRouteButton(City city, Callbacks callback) {
            return List.of(InlineKeyboardButton.builder()
                    .text(city.getName())
                    .callbackData(callback + SPLITTER + city.getId())
                    .build());
        }

        private static List<InlineKeyboardButton> makeMarkedRouteButton(City city, Callbacks cancelCallback) {
            return List.of(InlineKeyboardButton.builder()
                    .text(city.getName() + MARKER)
                    .callbackData(cancelCallback + SPLITTER + city.getId())
                    .build());
        }

    }

    public static class DateUtil {
        public static EditMessageText createEditMessageForSecondDate(CallbackQuery callbackQuery, LocalDate firstDate,
                                                                     String text, String callback) {
            List<List<InlineKeyboardButton>> buttons = createCalendar(firstDate, callback, firstDate, YES);
            return EditMessageText.builder()
                    .text(String.format(text, firstDate, firstDate.getMonth(), firstDate.getYear()))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageForSecondDate(CallbackQuery callbackQuery, LocalDate firstDate,
                                                                     String text, String callback, LocalDate invalidDate) {
            LocalDate date = LocalDate.now();
            List<List<InlineKeyboardButton>> buttons = createCalendar(date, callback, firstDate, YES, invalidDate, KRESTIK);
            return EditMessageText.builder()
                    .text(String.format(text, firstDate, date.getMonth(), date.getYear()))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .build();
        }

        public static SendMessage createSendMessageForFirstDate(Long chatId, String callback, String text) {
            LocalDate date = LocalDate.now();
            return SendMessage.builder()
                    .text(String.format(text, date.getMonth(), date.getYear()))
                    .chatId(chatId.toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(date, callback))
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageForFirstDate(CallbackQuery callbackQuery, String callback, String text) {
            LocalDate date = LocalDate.now();
            return EditMessageText.builder()
                    .text(String.format(text, date.getMonth(), date.getYear()))
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(date, callback))
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageTextForFirstDate(CallbackQuery callbackQuery, String callback,
                                                                        String text, LocalDate changedDate) {

            return EditMessageText.builder()
                    .text(String.format(text, changedDate.getMonth(), changedDate.getYear()))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(changedDate, callback))
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageTextForFirstDateWithIncorrectDate(CallbackQuery callbackQuery,
                                                                                         String callback, String text,
                                                                                         LocalDate incorrectDate) {
            LocalDate date = LocalDate.now();
            return EditMessageText.builder()
                    .text(String.format(text, date.getMonth(), date.getYear()))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(date, callback, incorrectDate, KRESTIK))
                            .build())
                    .build();
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, String callback) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtons(callback, numberDayInMonth, date))
                    .collect(Collectors.toList());
            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons);
            return buttons;
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, String callback,
                                                                      LocalDate chosenDate, String mark) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtonsWithReservedDate(callback, numberDayInMonth, date, chosenDate, mark))
                    .collect(Collectors.toList());
            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons);
            return buttons;
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, String callback,
                                                                      LocalDate chosenDate, String mark,
                                                                      LocalDate invalidDate, String invalidMark) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtonsWithReservedDate(callback, numberDayInMonth, date, chosenDate, mark,
                            invalidDate, invalidMark))
                    .collect(Collectors.toList());
            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons);
            return buttons;
        }

        private static List<LocalDate> createListOfDateWithPeriod(LocalDate localDate, Integer numberDaysInMonth) {
            List<LocalDate> list = localDate.withDayOfMonth(1)
                    .datesUntil(localDate.withDayOfMonth(localDate.getMonth().length(localDate.isLeapYear())), Period.ofDays(3))
                    .collect(Collectors.toList());
            if (numberDaysInMonth - list.get(list.size() - 1).getDayOfMonth() == 3)
                list.add(localDate.withDayOfMonth(numberDaysInMonth));
            return list;
        }

        private static List<InlineKeyboardButton> addRowOfButtons(String callback, int numberDaysInMonth, LocalDate date) {
            return getInlineKeyboardButtons(callback, numberDaysInMonth, date);
        }

        private static List<InlineKeyboardButton> addRowOfButtonsWithReservedDate(String callback, int numberDaysInMonth,
                                                                                  LocalDate date, LocalDate chosenDate, String mark) {
            return date.getMonth().equals(chosenDate.getMonth()) ? getInlineKeyboardButtonsTheSameMonth(callback, numberDaysInMonth, date, chosenDate, mark)
                    : getInlineKeyboardButtons(callback, numberDaysInMonth, date);
        }

        private static List<InlineKeyboardButton> addRowOfButtonsWithReservedDate(String callback, int numberDaysInMonth,
                                                                                  LocalDate date, LocalDate chosenDate, String mark,
                                                                                  LocalDate invalidDate, String invalidMark) {
            return (date.getMonth().equals(chosenDate.getMonth()) || date.getMonth().equals(invalidDate.getMonth())) ?
                    getInlineKeyboardButtonsTheSameMonth(callback, numberDaysInMonth, date, chosenDate, mark, invalidDate, invalidMark)
                    : getInlineKeyboardButtons(callback, numberDaysInMonth, date);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtons(String callback, int numberDaysInMonth, LocalDate date) {
            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
                return createDateRowWithThreeDays(callback, date);
            }
            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
                return createDateRowWithTwoDays(callback, date);
            }
            return createDateRowWithOneDay(callback, date);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtonsTheSameMonth(String callback, int numberDaysInMonth,
                                                                                       LocalDate date,
                                                                                       LocalDate chosenDate, String mark) {
            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
                return createDateRowWithThreeDays(callback, date, chosenDate, mark);
            }
            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
                return createDateRowWithTwoDays(callback, date, chosenDate, mark);
            }
            return createDateRowWithOneDay(callback, date, chosenDate, mark);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtonsTheSameMonth(String callback, int numberDaysInMonth,
                                                                                       LocalDate date,
                                                                                       LocalDate chosenDate, String mark,
                                                                                       LocalDate invalidDate, String invalidMark) {
            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
                return createDateRowWithThreeDays(callback, date, chosenDate, mark, invalidDate, invalidMark);
            }
            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
                return createDateRowWithTwoDays(callback, date, chosenDate, mark, invalidDate, invalidMark);
            }
            return createDateRowWithOneDay(callback, date, chosenDate, mark, invalidDate, invalidMark);
        }

        private static List<InlineKeyboardButton> createDateRowWithOneDay(String callback, LocalDate date) {
            return List.of(createDateButton(date, callback));
        }

        private static List<InlineKeyboardButton> createDateRowWithOneDay(String callback, LocalDate date,
                                                                          LocalDate chosenDate, String mark) {
            return List.of(createDateButton(date, callback, chosenDate, mark));
        }

        private static List<InlineKeyboardButton> createDateRowWithOneDay(String callback, LocalDate date,
                                                                          LocalDate chosenDate, String mark,
                                                                          LocalDate invalidDate, String invalidMark) {
            return List.of(createDateButton(date, callback, chosenDate, mark, invalidDate, invalidMark));
        }

        private static List<InlineKeyboardButton> createDateRowWithTwoDays(String callback, LocalDate date) {
            return List.of(createDateButton(date, callback),
                    createDateButton(date.plusDays(1), callback));
        }

        private static List<InlineKeyboardButton> createDateRowWithTwoDays(String callback, LocalDate date,
                                                                           LocalDate chosenDate, String mark) {
            return List.of(createDateButton(date, callback, chosenDate, mark),
                    createDateButton(date.plusDays(1), callback, chosenDate, mark));
        }

        private static List<InlineKeyboardButton> createDateRowWithTwoDays(String callback, LocalDate date,
                                                                           LocalDate chosenDate, String mark,
                                                                           LocalDate invalidDate, String invalidMark) {
            return List.of(createDateButton(date, callback, chosenDate, mark, invalidDate, invalidMark),
                    createDateButton(date.plusDays(1), callback, chosenDate, mark, invalidDate, invalidMark));
        }

        private static List<InlineKeyboardButton> createDateRowWithThreeDays(String callback, LocalDate date) {
            return List.of(createDateButton(date, callback),
                    createDateButton(date.plusDays(1), callback),
                    createDateButton(date.plusDays(2), callback));
        }

        private static List<InlineKeyboardButton> createDateRowWithThreeDays(String callback, LocalDate date,
                                                                             LocalDate chosenDate, String mark) {
            return List.of(createDateButton(date, callback, chosenDate, mark),
                    createDateButton(date.plusDays(1), callback, chosenDate, mark),
                    createDateButton(date.plusDays(2), callback, chosenDate, mark));
        }

        private static List<InlineKeyboardButton> createDateRowWithThreeDays(String callback, LocalDate date,
                                                                             LocalDate chosenDate, String mark,
                                                                             LocalDate invalidDate, String invalidMark) {
            return List.of(createDateButton(date, callback, chosenDate, mark, invalidDate, invalidMark),
                    createDateButton(date.plusDays(1), callback, chosenDate, mark, invalidDate, invalidMark),
                    createDateButton(date.plusDays(2), callback, chosenDate, mark, invalidDate, invalidMark));
        }

        private static void addLinksOnPreviousAndNextMonths(LocalDate localDate, String callback, List<List<InlineKeyboardButton>> buttons) {
            if (!localDate.getMonth().equals(LocalDate.now().getMonth())) {
                buttons.add(List.of(
                        createMonthButton(localDate, PREVIOUS_MONTH.name() + SPLITTER + callback, "Previous Month"),
                        createMonthButton(localDate, NEXT_MONTH.name() + SPLITTER + callback, "Next Month")));
            } else {
                buttons.add(List.of(
                        createMonthButton(localDate, NEXT_MONTH.name() + "&" + callback, "Next Month"))
                );
            }
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, java.lang.String callback) {
            return InlineKeyboardButton.builder()
                    .text(String.valueOf(localDate.getDayOfMonth()))
                    .callbackData(callback + SPLITTER + localDate)
                    .build();
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, String callback, LocalDate chosenDate, String mark) {
            return chosenDate.equals(localDate) ? InlineKeyboardButton.builder()
                    .text(localDate.getDayOfMonth() + mark)
                    .callbackData(getCancelCallback(callback) + SPLITTER + localDate)
                    .build() :
                    InlineKeyboardButton.builder()
                            .text(String.valueOf(localDate.getDayOfMonth()))
                            .callbackData(callback + SPLITTER + localDate)
                            .build();
        }

        private static Callbacks getCancelCallback(String callback) {
            switch (valueOf(callback.split(SPLITTER)[0])) {
                case SECOND_DATE_DRIVER:
                    return CANCEL_FIRST_DATE_DRIVER;
                case SECOND_DATE_PASSENGER:
                    return CANCEL_FIRST_DATE_PASSENGER;
                case FIRST_DATE_DRIVER:
                    return FIRST_DATE_DRIVER;
                case FIRST_DATE_PASSENGER:
                    return FIRST_DATE_PASSENGER;
                case CHANGE_FIRST_DATE_OF_OFFER:
                    return CHANGE_FIRST_DATE_OF_OFFER;
                case CHANGE_SECOND_DATE_OF_OFFER:
                    return CANCEL_FIRST_DATE_OF_OFFER;
                default:
                    throw new UnknownCommandException();
            }
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, String callback, LocalDate chosenDate, String mark,
                 LocalDate invalidDate, String invalidMark) {
            if (chosenDate.equals(localDate)) {
                return InlineKeyboardButton.builder()
                        .text(localDate.getDayOfMonth() + mark)
                        .callbackData(getCancelCallback(callback) + SPLITTER + localDate)
                        .build();
            } else if (invalidDate.equals(localDate)) {
                return InlineKeyboardButton.builder()
                        .text(localDate.getDayOfMonth() + invalidMark)
                        .callbackData(callback + SPLITTER + localDate)
                        .build();
            } else {
                return InlineKeyboardButton.builder()
                        .text(String.valueOf(localDate.getDayOfMonth()))
                        .callbackData(callback + SPLITTER + localDate)
                        .build();
            }
        }

        private static InlineKeyboardButton createMonthButton(LocalDate localDate, String callback, String text) {
            return InlineKeyboardButton.builder()
                    .text(text)
                    .callbackData(callback + SPLITTER + localDate)
                    .build();
        }

        public static boolean validFirstDate(LocalDate firstDate) {
            return LocalDate.now().isBefore(firstDate) || LocalDate.now().isEqual(firstDate);
        }

        public static boolean validSecondDate(LocalDate firstDate, LocalDate secondDate) {
            return secondDate.isAfter(firstDate);
        }
    }

    private static List<List<InlineKeyboardButton>> getButtonsList() {
        return new ArrayList<>();
    }

    public static String getCallbackValue(String dataCallback) {
        return dataCallback.split(SPLITTER)[1];
    }

    private static SendMessage getSendMessage(Long chatId, List<List<InlineKeyboardButton>> buttons, String text) {
        return SendMessage.builder()
                .text(text)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(buttons)
                        .build())
                .chatId(chatId.toString())
                .build();
    }

    public static EditMessageText createEditMessageTextAfterConfirm(CallbackQuery callbackQuery, Callbacks callback, String message) {
        List<List<InlineKeyboardButton>> buttons = List.of(List.of(
                InlineKeyboardButton.builder()
                        .text(SAVE_WITHOUT_DESCRIPTION)
                        .callbackData(callback.name())
                        .build()));
        return EditMessageText.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .text(message)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    public static SendMessage createSendMessageWithDoubleCheckOffer(CallbackQuery callbackQuery,
                                                                    List<? extends Request> requests,
                                                                    Callbacks checkCallback,
                                                                    Callbacks cancelCallback) {
        String requestsInf = requests.isEmpty() ? NO_SUITABLE_OFFERS :
                String.format(SUITABLE_OFFERS, getListOfOffersForRequest(requests));
        List<List<InlineKeyboardButton>> buttons = List.of(List.of(
                InlineKeyboardButton.builder()
                        .text(SAVE)
                        .callbackData(checkCallback.name() + SPLITTER)
                        .build(),
                cancelRequest(cancelCallback)));

        return getSendMessage(callbackQuery.getMessage().getChatId(), buttons,
                String.format(ASK_FOR_DESIRE_TO_SAVE, requestsInf));
    }

    private static InlineKeyboardButton cancelRequest(Callbacks cancelCallback) {
        return InlineKeyboardButton.builder()
                .text(MENU)
                .callbackData(cancelCallback.name() + SPLITTER)
                .build();
    }

    public static SendMessage makeSendMessageForUserWithoutUsername(Message message) {
        return SendMessage.builder()
                .text(HAVE_NO_USERNAME)
                .chatId(message.getChatId().toString())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboardRow(List.of(InlineKeyboardButton.builder()
                                .text(USERNAME_IS_ADDED)
                                .callbackData(MAIN_MENU.name() + SPLITTER + "checkUsername")
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
                                .callbackData(MAIN_MENU.name())
                                .build())).build())
                .build();
    }

    public static String getListOfOffersForRequest(List<? extends Request> requests) {
        return requests.stream()
                .map(CallbackUtil::getOffersViewForRequest)
                .collect(Collectors.joining("\n\n"));
    }

    public static String getOffersView(Request request) {
        return request.getDescription() != null ?
                String.format(OFFER_OF_CHANGING_OFFER_PATTERN, getCurrentRoute(request.getCities()), request.getFirstDate(), request.getSecondDate(),
                        request.getActivity(), request.getDescription()) :
                String.format(OFFER_OF_CHANGING_OFFER_PATTERN_WITHOUT_DESC, getCurrentRoute(request.getCities()), request.getFirstDate(), request.getSecondDate(),
                        request.getActivity());
    }

    public static String getOffersViewForRequest(Request request) {
        return request.getDescription() != null ?
                String.format(OFFERS_FOR_REQUESTS_PATTERN, getCorrectName(request.getUserDto().getFirstName()),
                        getCorrectName(request.getUserDto().getLastName()), getCurrentRoute(request.getCities()),
                        request.getFirstDate(), request.getSecondDate(),
                        request.getActivity(), request.getDescription(), request.getUserDto().getUserName()) :
                String.format(OFFERS_FOR_REQUESTS_PATTERN_WITHOUT_DESC, getCorrectName(request.getUserDto().getFirstName()),
                        getCorrectName(request.getUserDto().getLastName()), getCurrentRoute(request.getCities()), request.getFirstDate(),
                        request.getSecondDate(), request.getActivity(), request.getUserDto().getUserName());
    }

    private static String getCorrectName(String name) {
        return name == null ?
                EMPTY :
                name;
    }

    public static EditMessageText getAvailableOffersList(List<? extends Request> requests, CallbackQuery callbackQuery, String message) {
        String requestsInf = requests.isEmpty() ? NO_SUITABLE_OFFERS :
                String.format(SUITABLE_OFFERS, getListOfOffersForRequest(requests));
        return EditMessageText.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .text(message + requestsInf)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(InlineKeyboardButton.builder()
                                .text("Back to main menu")
                                .callbackData(Callbacks.MAIN_MENU.name())
                                .build())))
                        .build())
                .build();
    }

}
