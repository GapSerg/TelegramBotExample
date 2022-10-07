package com.godeltech.springgodelbot.util;

public class ConstantUtil {

    public static final String START_MESSAGE = "You can choose a several options. \n\nIf you press 'DRIVER'" +
            " button you'll get opportunities to create a request as a driver and find passengers in the route and dates you are interested in.\n\n" +
            "If you press 'Passenger' button you'll get opportunities to create a request as a passenger and find driver in the route and dates you are interested in\n\n" +
            "If you press 'List of my offers' button, you'll get opportunities to check your already created offers as 'DRIVER' or as 'PASSENGER' and if you want to change some data in them";

    public final static String NO_FOUND_PATTERN = "%s wasn't found by %s=%s";
    public final static String AUTHORIZATION_PATTERN = "%s wasn't authorize cause %s=%s";
    public final static String UNKNOWN_COMMAND = "Sorry but the command is incorrect";
    public final static String OFFERS_FOR_REQUESTS_PATTERN = "User : %s %s \nRoute: %s \uD83D\uDE82 \nDate: %s ➖ %s ⏰ \nRole: %s \nDescription: %s \uD83D\uDCC4 \nhttps://t.me/%s";
    public final static String OFFERS_FOR_REQUESTS_PATTERN_WITHOUT_DESC = "User : %s %s \nRoute: %s \uD83D\uDE82 \nDate: %s ➖ %s ⏰ \nRole: %s \uD83D\uDCC4 \nhttps://t.me/%s";
    public final static String OFFER_OF_CHANGING_OFFER_PATTERN = "Route: %s \uD83D\uDE82 \nDate: %s ➖ %s ⏰ \nRole: %s \nDescription: %s \uD83D\uDCC4";
    public final static String OFFER_OF_CHANGING_OFFER_PATTERN_WITHOUT_DESC = "Route: %s \uD83D\uDE82 \nDate: %s ➖ %s ⏰ \nRole: %s \n";
    public final static String OFFERS_OF_DRIVERS_PATTERN = "%s \uD83D\uDE82 \n";
    public static final String SAVE_WITHOUT_DESCRIPTION = "Save without description";
    public static final String SUITABLE_OFFERS = "Here are current offers, that may be suitable :\n%s";
    public static final String ASK_FOR_DESIRE_TO_SAVE = " Do you want to save your request ?  \n\n%s";
    public static final String NO_SUITABLE_OFFERS = "There are no offers with these route and dates yet";

    public static final String INCORRECT_FIRST_DATE = "You've chosen the wrong date. Please choose the correct date, \nThe current month and year : %s - %s";
    public static final String CHOSEN_SECOND_DATE = "Chosen dates are :  %s - %s";
    public static final String CHOSEN_FIRST_DATE = "Chosen date is : %s";
    public static final String CHOOSE_THE_FIRST_DATE = "Choose the first date.\nCurrent month and year %s - %s :";

    public static final String WRITE_ADD_DESCRIPTION_FOR_PASSENGER = "You can write description and press enter and we will save your request or just save without description";
    public static final String WRITE_ADD_DESCRIPTION_FOR_DRIVER = "You can write additional description and press the enter or press save button and we save without add description";
    public static final String SUCCESSFUL_REQUEST_SAVING = "We successful saved your request. Thanks, have a good day";
    public static final String OFFER_WAS_DELETED = "The offer was deleted";
    public static final String WRITE_ADDITIONAL_DESCRIPTION_FOR_CHANGE = "Please write additional description and press enter";
    public static final String DESCRIPTION_WAS_UPDATED = "The description was updated";
    public static final String DATES_WERE_CHANGED = "The dates were changed \n";
    public static final String CHOSEN_DATE = "Date : %s : %s";
    public static final String CHOSEN_ROLE = "Role : %s";
    public static final String CHOSE_THE_ROUTE = "Chose the place of departure that you are interested in";
    public static final String CURRENT_ROUTE = "Please select a place of arrival or a stopping point.\nYour current route : %s\nIf you pressed on reserved button we'll cancel it";
    public static final String SELECTED_ROUTE = "Selected route : %s";
    public static final String FINISH = "FINISH";
    public static final String ROUTE_CHANGED = "You've successfully changed route of offer \n";
    public static final String SAVE = "SAVE";

    public static final String DATE_FORMAT="%s-%s";

}
