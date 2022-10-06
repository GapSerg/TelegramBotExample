package com.godeltech.springgodelbot.util;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
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


        public static SendMessage createRouteSendMessage(List<City> cities, Integer callback, Integer cancelRequestCallback, Long chatId, String token) {
            List<List<InlineKeyboardButton>> buttons = getButtonsList();

            cities.forEach(route -> buttons.add(List.of(
                    InlineKeyboardButton.builder()
                            .text(route.getName())
                            .callbackData(callback + SPLITTER + token + SPLITTER + route.getId())
                            .build()
            )));
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token, "Back to menu")));
            return getSendMessage(chatId, buttons, CHOSE_THE_ROUTE);
        }


        public static BotApiMethod createEditSendMessageForRoutes(CallbackQuery callbackQuery,
                                                                  List<City> cities,
                                                                  List<City> reservedCities,
                                                                  Integer callback, Integer cancelRouteCallback, Integer cancelRequestCallback, String token) {
            List<List<InlineKeyboardButton>> buttons = getRouteButtons(cities, reservedCities, callback, cancelRouteCallback, cancelRequestCallback, token);
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

        private static List<List<InlineKeyboardButton>> getRouteButtons(List<City> cities, List<City> reservedCities, Integer callback, Integer cancelRouteCallback, Integer cancelRequestCallback, String token) {
            List<List<InlineKeyboardButton>> buttons = cities.stream()
                    .map(route -> reservedCities.contains(route) ?
                            makeMarkedRouteButton(route, cancelRouteCallback, token, reservedCities.lastIndexOf(route)) :
                            makeUnmarkedRouteButton(route, callback, token))
                    .collect(Collectors.toList());
            if (reservedCities.size() >= 2) {
                buttons.add(List.of(InlineKeyboardButton.builder()
                                .text(FINISH)
                                .callbackData(getChoseDateCallback(callback, token))
                                .build(),
                        getCancelButton(cancelRequestCallback, token, getCancelText(cancelRequestCallback))));
            } else {
                buttons.add(List.of(getCancelButton(cancelRequestCallback, token, getCancelText(cancelRequestCallback))));
            }
            return buttons;
        }

        public static String getCurrentRoute(List<City> reservedCities) {
            return reservedCities
                    .stream()
                    .map(City::getName)
                    .collect(Collectors.joining("➡"));
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

        private static List<InlineKeyboardButton> makeUnmarkedRouteButton(City city, Integer callback, String token) {
            return List.of(InlineKeyboardButton.builder()
                    .text(city.getName())
                    .callbackData(callback + SPLITTER + token + SPLITTER + city.getId())
                    .build());
        }

        private static List<InlineKeyboardButton> makeMarkedRouteButton(City city, Integer cancelCallback, String token, int index) {
            return List.of(InlineKeyboardButton.builder()
                    .text(getStartPhrase(index) + city.getName() + MARKER)
                    .callbackData(cancelCallback + SPLITTER + token + SPLITTER + city.getId())
                    .build());
        }

        private static String getStartPhrase(int index) {
            return index > 0 ?
                    "TO:  " :
                    "FROM: ";
        }
    }

    private static String getCancelText(Integer cancelRequestCallback) {
        return cancelRequestCallback.equals(RETURN_TO_CHANGE_OF_OFFER.ordinal())?
                "Back" : "Back to menu";
    }

    public static InlineKeyboardButton getCancelButton(Integer cancelRequestCallback, String token, String message) {
        return InlineKeyboardButton.builder()
                .text(message)
                .callbackData(cancelRequestCallback + SPLITTER + token)
                .build();
    }

    public static class DateUtil {
        public static EditMessageText createEditMessageForSecondDate(CallbackQuery callbackQuery, LocalDate firstDate,
                                                                     String text, Integer callback, Integer cancelRequestCallback, String token) {
            List<List<InlineKeyboardButton>> buttons = createCalendar(firstDate, callback, cancelRequestCallback, firstDate, YES, token);
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
                                                                     String text, Integer callback, Integer cancelRequestCallback,
                                                                     LocalDate invalidDate, String token) {
            LocalDate date = LocalDate.now();
            List<List<InlineKeyboardButton>> buttons = createCalendar(date, callback, cancelRequestCallback, firstDate, YES, invalidDate, KRESTIK, token);
            return EditMessageText.builder()
                    .text(String.format(text, firstDate, date.getMonth(), date.getYear()))
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .build();
        }

        public static SendMessage createSendMessageForFirstDate(Long chatId, Integer callback, Integer cancelRequestCallback,
                                                                String text, String token) {
            LocalDate date = LocalDate.now();
            List<List<InlineKeyboardButton>> buttons = createCalendar(date, callback, cancelRequestCallback, token);
            return SendMessage.builder()
                    .text(String.format(text, date.getMonth(), date.getYear()))
                    .chatId(chatId.toString())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(buttons)
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageForFirstDate(CallbackQuery callbackQuery, Integer callback,
                                                                    Integer cancelRequestCallback, String text, String token) {
            LocalDate date = LocalDate.now();
            return EditMessageText.builder()
                    .text(String.format(text, date.getMonth(), date.getYear()))
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(createCalendar(date, callback, cancelRequestCallback, token))
                            .build())
                    .build();
        }

        public static EditMessageText createEditMessageTextForFirstDate(CallbackQuery callbackQuery, Integer callback,
                                                                        Integer cancelRequestCallback,
                                                                        String text, LocalDate changedDate, String token) {

            return EditMessageText.builder()
                    .text(String.format(text, changedDate.getMonth(), changedDate.getYear()))
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
                            .keyboard(createCalendar(date, callback, cancelRequestCallback, incorrectDate, KRESTIK, token))
                            .build())
                    .build();
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, Integer callback, Integer cancelRequestCallback, String token) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtons(callback, numberDayInMonth, date, token))
                    .collect(Collectors.toList());
            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons, token);
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token,  getCancelText(cancelRequestCallback))));
            return buttons;
        }

        public static List<List<InlineKeyboardButton>> createCalendar(LocalDate localDate, Integer callback, Integer cancelRequestCallback,
                                                                      LocalDate chosenDate, String mark, String token) {
            int numberDayInMonth = localDate.getMonth().length(localDate.isLeapYear());
            List<List<InlineKeyboardButton>> buttons = createListOfDateWithPeriod(localDate, numberDayInMonth).stream()
                    .map(date -> addRowOfButtonsWithReservedDate(callback, numberDayInMonth, date, chosenDate, mark, token))
                    .collect(Collectors.toList());
            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons, token);
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token,  getCancelText(cancelRequestCallback))));
            return buttons;
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
            addLinksOnPreviousAndNextMonths(localDate.withDayOfMonth(1), callback, buttons, token);
            buttons.add(List.of(getCancelButton(cancelRequestCallback, token,  getCancelText(cancelRequestCallback))));
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

        private static List<InlineKeyboardButton> addRowOfButtons(Integer callback, int numberDaysInMonth, LocalDate date, String token) {
            return getInlineKeyboardButtons(callback, numberDaysInMonth, date, token);
        }

        private static List<InlineKeyboardButton> addRowOfButtonsWithReservedDate(Integer callback, int numberDaysInMonth,
                                                                                  LocalDate date, LocalDate chosenDate, String mark, String token) {
            return date.getMonth().equals(chosenDate.getMonth()) ?
                    getInlineKeyboardButtonsTheSameMonth(callback, numberDaysInMonth, date, chosenDate, mark, token)
                    : getInlineKeyboardButtons(callback, numberDaysInMonth, date, token);
        }

        private static List<InlineKeyboardButton> addRowOfButtonsWithReservedDate(Integer callback, int numberDaysInMonth,
                                                                                  LocalDate date, LocalDate chosenDate, String mark,
                                                                                  LocalDate invalidDate, String invalidMark, String token) {
            return (date.getMonth().equals(chosenDate.getMonth()) || date.getMonth().equals(invalidDate.getMonth())) ?
                    getInlineKeyboardButtonsTheSameMonth(callback, numberDaysInMonth, date, chosenDate, mark, invalidDate, invalidMark, token)
                    : getInlineKeyboardButtons(callback, numberDaysInMonth, date, token);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtons(Integer callback, int numberDaysInMonth, LocalDate date, String token) {
            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
                return createDateRowWithThreeDays(callback, date, token);
            }
            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
                return createDateRowWithTwoDays(callback, date, token);
            }
            return createDateRowWithOneDay(callback, date, token);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtonsTheSameMonth(Integer callback, int numberDaysInMonth,
                                                                                       LocalDate date,
                                                                                       LocalDate chosenDate, String mark, String token) {
            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
                return createDateRowWithThreeDays(callback, date, chosenDate, mark, token);
            }
            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
                return createDateRowWithTwoDays(callback, date, chosenDate, mark, token);
            }
            return createDateRowWithOneDay(callback, date, chosenDate, mark, token);
        }

        private static List<InlineKeyboardButton> getInlineKeyboardButtonsTheSameMonth(Integer callback, int numberDaysInMonth,
                                                                                       LocalDate date,
                                                                                       LocalDate chosenDate, String mark,
                                                                                       LocalDate invalidDate, String invalidMark, String token) {
            if (numberDaysInMonth - date.getDayOfMonth() >= 2) {
                return createDateRowWithThreeDays(callback, date, chosenDate, mark, invalidDate, invalidMark, token);
            }
            if (numberDaysInMonth - date.getDayOfMonth() == 1) {
                return createDateRowWithTwoDays(callback, date, chosenDate, mark, invalidDate, invalidMark, token);
            }
            return createDateRowWithOneDay(callback, date, chosenDate, mark, invalidDate, invalidMark, token);
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

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, Integer callback, String token) {
            return InlineKeyboardButton.builder()
                    .text(String.valueOf(localDate.getDayOfMonth()))
                    .callbackData(callback + SPLITTER + token + SPLITTER + localDate)
                    .build();
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, LocalDate chosenDate, Integer callback, String mark, String token) {
            return chosenDate.equals(localDate) ? InlineKeyboardButton.builder()
                    .text(localDate.getDayOfMonth() + mark)
                    .callbackData(getCancelCallback(callback) + SPLITTER + token + SPLITTER + localDate)
                    .build() :
                    InlineKeyboardButton.builder()
                            .text(String.valueOf(localDate.getDayOfMonth()))
                            .callbackData(callback + SPLITTER + token + SPLITTER + localDate)
                            .build();
        }

        private static Integer getCancelCallback(Integer callback) {
            switch (Callbacks.values()[callback]) {
                case SECOND_DATE_DRIVER:
                    return CANCEL_FIRST_DATE_DRIVER.ordinal();
                case SECOND_DATE_PASSENGER:
                    return CANCEL_FIRST_DATE_PASSENGER.ordinal();
                case FIRST_DATE_DRIVER:
                    return FIRST_DATE_DRIVER.ordinal();
                case FIRST_DATE_PASSENGER:
                    return FIRST_DATE_PASSENGER.ordinal();
                case CHANGE_FIRST_DATE_OF_OFFER:
                    return CHANGE_FIRST_DATE_OF_OFFER.ordinal();
                case CHANGE_SECOND_DATE_OF_OFFER:
                    return CANCEL_FIRST_DATE_OF_OFFER.ordinal();
                default:
                    throw new UnknownCommandException();
            }
        }

        private static InlineKeyboardButton createDateButton
                (LocalDate localDate, Integer callback, LocalDate chosenDate, String mark,
                 LocalDate invalidDate, String invalidMark, String token) {
            if (chosenDate.equals(localDate)) {
                return InlineKeyboardButton.builder()
                        .text(localDate.getDayOfMonth() + mark)
                        .callbackData(getCancelCallback(callback)+ SPLITTER + token + SPLITTER + localDate)
                        .build();
            } else if (invalidDate.equals(localDate)) {
                return InlineKeyboardButton.builder()
                        .text(localDate.getDayOfMonth() + invalidMark)
                        .callbackData(callback + SPLITTER + token + SPLITTER + localDate)
                        .build();
            } else {
                return InlineKeyboardButton.builder()
                        .text(String.valueOf(localDate.getDayOfMonth()))
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
        return dataCallback.split(SPLITTER)[2];
    }

    public static String getCallbackToken(String dataCallback) {
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

    public static SendMessage createSendMessageWithDoubleCheckOffer(CallbackQuery callbackQuery,
                                                                    List<? extends Request> requests,
                                                                    Integer checkCallback,
                                                                    Integer cancelCallback, String token) {
        String requestsInf = requests.isEmpty() ? NO_SUITABLE_OFFERS :
                String.format(SUITABLE_OFFERS, getListOfOffersForRequest(requests));
        List<List<InlineKeyboardButton>> buttons = List.of(List.of(
                getCancelButton(checkCallback, token, SAVE),
                cancelRequest(cancelCallback, token)));

        return getSendMessage(callbackQuery.getMessage().getChatId(), buttons,
                String.format(ASK_FOR_DESIRE_TO_SAVE, requestsInf));
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

    public static EditMessageText getAvailableOffersList(List<? extends Request> requests, CallbackQuery callbackQuery, String message, String token) {
        String requestsInf = requests.isEmpty() ? NO_SUITABLE_OFFERS :
                String.format(SUITABLE_OFFERS, getListOfOffersForRequest(requests));
        return EditMessageText.builder()
                .messageId(callbackQuery.getMessage().getMessageId())
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .text(message + requestsInf)
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(List.of(getCancelButton(MAIN_MENU.ordinal(), token, "Back to main menu"))))
                        .build())
                .build();
    }

    public static EditMessageText getEditTextMessageForOffer(CallbackQuery callbackQuery, String token, ChangeOfferRequest request) {
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text(getOffersView(request))
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(getChangeOfferButtons(request, token))
                        .build())
                .build();
    }

    private static List<List<InlineKeyboardButton>> getChangeOfferButtons(ChangeOfferRequest request, String token) {
        return List.of(List.of(InlineKeyboardButton.builder()
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

}
