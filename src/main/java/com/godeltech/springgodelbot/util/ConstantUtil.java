package com.godeltech.springgodelbot.util;

public class ConstantUtil {

    public static final String START_MESSAGE = "Welcome aboard! \n If you press  the 'DRIVER'" +
            "button, you'll get the opportunity to create a request as a driver and  find passengers for the route and dates you are interested in.\n\n" +
            "If you press the 'Passenger' button, you'll get the opportunity to create a request as a passenger and find a driver for the route and dates you are interested in\n\n" +
            "If you press the 'List of my offers' button, you'll have the possibility to check your already created offers as a 'DRIVER' or as 'PASSENGER' and change some data if necessary";
    public final static String NO_UNIQUE_PATTERN = "%s already exists with %s=%s";
    public final static String NO_FOUND_PATTERN = "%s wasn't found by %s=%s";
    public final static String NO_FOUND_PATTERN_WITHOUT_VALUES = "%s wasn't found";
    public static final String MEMBERSHIP_PATTERN = "User with id = %s, and username = %s isn't a member of chmoki group";
    public final static String AUTHORIZATION_PATTERN = "%s wasn't authorize cause %s=%s";
    public final static String UNKNOWN_COMMAND = "Sorry but the command is incorrect";
    public final static String OFFERS_FOR_REQUESTS_PATTERN = "\nUser : %s %s \nRole: %s \nRoute: %s \n%s ⏰ \nDescription: %s \uD83D\uDCC4 \nhttps://t.me/%s";
    public final static String OFFERS_FOR_REQUESTS_PATTERN_WITHOUT_DESC = "\nUser : %s %s\nRole: %s  \nRoute: %s \n%s ⏰  \nhttps://t.me/%s";
    public final static String OFFER_OF_CHANGING_OFFER_PATTERN = "Your current offer:\nRole: %s \nRoute: %s  \n%s ⏰ \nDescription: %s \uD83D\uDCC4";
    public final static String OFFER_OF_CHANGING_OFFER_PATTERN_WITHOUT_DESC = "Your current offer:\nRole: %s \nRoute: %s \n%s ⏰ \n";
    public static final String SAVE_WITHOUT_DESCRIPTION = "Save without description";
    public static final String SUITABLE_OFFERS = "%sYour offer :\nRole : %s\nSelected route : %s\n%s ⏰\n%s\n\nHere are current offers, that may be suitable :\n\n%s";
    public static final String ASK_FOR_DESIRE_TO_SAVE = " Do you want to save your request ?  \n\n%s";
    public static final String NO_SUITABLE_OFFERS = "%sYour offer :\nRole : %s\nSelected route : %s\n%s ⏰\n%s\nThere are no offers with these route and dates yet";
    public static final String CREATED_REQUEST = "The request was created\n";
    public static final String CHOSEN_SECOND_DATE = "Role : %s \nSelected route : %s \nChosen dates are :  %s - %s";
    public static final String CHOSEN_FIRST_DATE = "Role : %s \nSelected route : %s \nChosen date is : %s\n If you aren't sure about the date, you can choose a second date";
    public static final String CHOOSE_THE_FIRST_DATE = "Role : %s \nSelected route : %s \nChoose the date.\n";
    public static final String CHOSEN_FIRST_DATE_OF_OFFER = "Chosen date is : %s\n If you aren't sure about the date, you can choose a second date";

    public static final String WRITE_ADD_DESCRIPTION_FOR_PASSENGER = "Post the description or save the request without description";
    public static final String WRITE_ADD_DESCRIPTION_FOR_DRIVER = "Post the description or save the trip without description";
    public static final String SUCCESSFUL_REQUEST_SAVING = "We successful saved your request.\n";
    public static final String OFFER_WAS_DELETED = "The offer was deleted";
    public static final String WRITE_ADDITIONAL_DESCRIPTION_FOR_CHANGE = "Please write additional description and press enter";
    public static final String DATES_WERE_CHANGED = "The dates were changed \n";
    public static final String NO_CHOSEN_DATE = "Date :";
    public static final String CHOSEN_DATE = "Date : %s";
    public static final String CHOSEN_DATES ="Date : %s - %s";
    public static final String CHOSEN_ROLE = "Role : %s \nChose the place FROM";
    public static final String CHOSE_THE_ROUTE = "Role : %s \nChose the place TO";
    public static final String CURRENT_ROUTE = "Role : %s \nChose the place TO or a stopping point.\nYour current route : %s\nIf you pressed the selected button we'll cancel it";
    public static final String CHOSE_THE_ROUTE_OF_OFFER = "Chose the place FROM";
    public static final String CURRENT_ROUTE_OF_OFFER = "Chose the place TO.\nYour current route : %s\nIf you pressed the selected  button we'll cancel it";
    public static final String HAVE_NO_USERNAME = "You don't have a username, please add it in your personal settings.When you deal with it,  just press the button";
    public static final String USERNAME_IS_ADDED = "I've added my username";
    public static final String SAVE = "SAVE";
    public static final String NEXT = "Next";
    public static final String DATE_FORMAT="%s-%s";

    public static final String SPLITTER = "&";
    public static final String INCORRECT_MARKER = "❌";
    public static final String CORRECT_MARKER = "✅";
    public static final String MENU = "MENU";
    public static final String EMPTY = " ";

    public static final String START = "/start";
    public static final String HELP = "/help";
    public static final String START_DESCRIPTION = "Info to start using this bot";
    public static final String HELP_DESCRIPTION = "What is this bot for";
    public static final String CHOOSE_THE_OPTION = "\nChoose the option you are interested in";
    public static final String OFFERS_LIST = "List of my offers";
    public static final String HELP_MESSAGE = "This bot was created for people who need help moving to some place or deliver a pack or a bag and for those who can offer this help";
    public static final String BOT_COMMAND = "bot_command";
    public static final String START_ENTITY = "/start";
    public static final String HELP_ENTITY = "/help";

}
