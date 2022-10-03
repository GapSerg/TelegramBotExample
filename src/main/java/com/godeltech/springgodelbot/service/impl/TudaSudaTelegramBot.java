package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Offer;
import com.godeltech.springgodelbot.service.CallbackResolverService;
import com.godeltech.springgodelbot.service.MessageResolverService;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updates.SetWebhook;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.starter.SpringWebhookBot;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.OFFERS_FOR_REQUESTS_PATTERN_WITHOUT_DESC;
import static com.godeltech.springgodelbot.util.ConstantUtil.OFFER_OF_CHANGING_OFFER_PATTERN;

@Slf4j
@Getter
@Setter
public class TudaSudaTelegramBot extends SpringWebhookBot {

    private final MessageResolverService messageResolverService;
    private final CallbackResolverService callbackResolverService;

    private String botUsername;
    private String botToken;
    private String botPath;

    public TudaSudaTelegramBot(SetWebhook setWebhookInstance, MessageResolverService messageResolverService, CallbackResolverService callbackResolverService) {
        super(setWebhookInstance);
        this.messageResolverService = messageResolverService;
        this.callbackResolverService = callbackResolverService;
    }

    public TudaSudaTelegramBot(SetWebhook setWebhookInstance, DefaultBotOptions defaultBotOptions, MessageResolverService messageResolverService, CallbackResolverService callbackResolverService) {
        super(defaultBotOptions, setWebhookInstance);
        this.messageResolverService = messageResolverService;
        this.callbackResolverService = callbackResolverService;
    }

    @Override
    @SneakyThrows
    public BotApiMethod<?> onWebhookUpdateReceived(Update update) {

        BotApiMethod botApiMethod = null;
        if (update.hasMessage()) {
            botApiMethod = messageResolverService.handleMessage(update.getMessage());
            return botApiMethod;
        } else if (update.hasCallbackQuery()) {
            botApiMethod = callbackResolverService.handleCallBack(update.getCallbackQuery());
            return botApiMethod;
        }
        return botApiMethod;
    }

    public void makeSendMessageForOfferListOfSupplier(List<Offer> offers, Long chatId) {
        offers.stream()
                .map(supplier -> SendMessage.builder()
                        .text(String.format(OFFER_OF_CHANGING_OFFER_PATTERN, supplier.getCities().stream().map(City::getName)
                                        .collect(Collectors.joining("-")), supplier.getFirstDate(), supplier.getSecondDate(),
                                supplier.getDescription()))
                        .replyMarkup(InlineKeyboardMarkup.builder()
                                .keyboard(getChangeOfferButtons(supplier))
                                .build())
                        .chatId(chatId.toString())
                        .build())
                .forEach(sendMessage -> {
                    try {
                        execute(sendMessage);
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                });
    }


    public void makeSendMessageAvailableOffersForConsumer(List<Offer> offers, Long chatId) {
        offers.forEach(supplier -> {
            try {
                execute(SendMessage.builder()
                        .chatId(chatId.toString())
                        .text(String.format(OFFERS_FOR_REQUESTS_PATTERN_WITHOUT_DESC, supplier.getUserEntity().getFirstName(),
                                supplier.getUserEntity().getLastName(), supplier.getCities().stream().map(City::getName)
                                        .collect(Collectors.joining("-")), supplier.getFirstDate(), supplier.getSecondDate(),
                                supplier.getDescription(), supplier.getUserEntity().getUserName()))
                        .build());
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        });
    }

    private List<List<InlineKeyboardButton>> getChangeOfferButtons(Offer offer) {
        return List.of(List.of(InlineKeyboardButton.builder()
                                .text("Change route")
                                .callbackData(CHANGE_ROUTE_OF_OFFER.name() + "&" + offer.getId())
                                .build(),
                        InlineKeyboardButton.builder()
                                .text("Change date")
                                .callbackData(CHANGE_DATE_OF_OFFER.name() + "&" + offer.getId())
                                .build()
                ),
                List.of(InlineKeyboardButton.builder()
                        .text("Change description")
                        .callbackData(CHANGE_DESCRIPTION_OF_OFFER.name() + "&" + offer.getId())
                        .build(), InlineKeyboardButton.builder()
                        .text("Delete offer")
                        .callbackData(DELETE_OFFER.name() + "&" + offer.getId())
                        .build()));
    }

    public void editPreviousMessage(CallbackQuery callbackQuery, String answer) {
        try {
            execute(EditMessageText.builder()
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text(answer)
                    .build());
        } catch (TelegramApiException e) {
            log.error("There is a tg exception: {}", e.getMessage());
            throw new UnknownCommandException(callbackQuery.getMessage());
        }
    }

    public void deleteMessages(Long chatId, Set<Integer> messages) {
        messages.forEach(message ->
                deleteMessage(chatId, message)
        );
        messages.clear();
    }

    public void deleteMessage(Long chatId, Integer message) {
        try {
            execute(DeleteMessage.builder()
                    .chatId(chatId.toString())
                    .messageId(message)
                    .build());
        } catch (TelegramApiException e) {
            throw new UnknownCommandException();
        }
    }
}