package com.godeltech.springgodelbot.util;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import liquibase.repackaged.org.apache.commons.lang3.RandomStringUtils;
import liquibase.repackaged.org.apache.commons.text.RandomStringGenerator;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;

public class BotMenu {

    public static final String START = "/start";
    public static final String HELP = "/help";
    public static final String START_DESCRIPTION = "get a welcome message";
    public static final String HELP_DESCRIPTION = "info how to use this bot";

    public static List<BotCommand> getCommands() {
        List<BotCommand> listOfCommands = new ArrayList<>();
        listOfCommands.add(createBotCommand(START, START_DESCRIPTION));
        listOfCommands.add(createBotCommand(HELP, HELP_DESCRIPTION));
        return listOfCommands;
    }

    private static BotCommand createBotCommand(java.lang.String command, java.lang.String description) {
        return BotCommand.builder()
                .command(command)
                .description(description)
                .build();
    }

    public static EditMessageText getStartMenu(Message message, String text) {
        List<List<InlineKeyboardButton>> buttons = getStartMenuButtons();
        return EditMessageText.builder()
                .chatId(message.getChatId().toString())
                .messageId(message.getMessageId())
                .text(text + "\nChoose the option you are interested in")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    public static EditMessageText getStartMenu(ChangeOfferRequest changeOfferRequest, String text) {
        List<List<InlineKeyboardButton>> buttons = getStartMenuButtons();
        return EditMessageText.builder()
                .chatId(changeOfferRequest.getChatId().toString())
                .messageId(changeOfferRequest.getMessages().stream().findFirst().orElseThrow(UnknownCommandException::new))
                .text(text + "\nChoose the option you are interested in")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    public static EditMessageText getStartMenu(CallbackQuery callbackQuery) {
        List<List<InlineKeyboardButton>> buttons = getStartMenuButtons();
        return EditMessageText.builder()
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .text("Choose the role you are interested in")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    public static SendMessage getStartMenu(Long chatId, String text) {
        List<List<InlineKeyboardButton>> buttons = getStartMenuButtons();
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text(text + "\n" + "Choose the option you are interested in")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    public static SendMessage getStartMenu(Long chatId) {
        List<List<InlineKeyboardButton>> buttons = getStartMenuButtons();
        return SendMessage.builder()
                .chatId(chatId.toString())
                .text("Choose the option you are interested in")
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(buttons)
                        .build())
                .build();
    }

    private static List<List<InlineKeyboardButton>> getStartMenuButtons() {
        List<List<InlineKeyboardButton>> buttons = new ArrayList<>();
        String uuid = createUUIDToken();
        Arrays.stream(Activity.values())
                .map(activity -> List.of(
                        InlineKeyboardButton.builder()
                                .text(activity.name())
                                .callbackData(Callbacks.ACTIVITY.ordinal()+SPLITTER+uuid + SPLITTER + activity)
                                .build()))
                .forEach(buttons::add);
        buttons.add(List.of(
                InlineKeyboardButton.builder()
                        .text("List of my offers")
                        .callbackData(Callbacks.OFFERS_ACTIVITY.ordinal()+SPLITTER+uuid)
                        .build()));
        return buttons;
    }
    private static String createUUIDToken(){
        String token = UUID.randomUUID().toString().replaceAll("-","").substring(6);
        return token;
    }
}
