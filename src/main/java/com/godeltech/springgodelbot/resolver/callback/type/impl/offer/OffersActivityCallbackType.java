package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;

@Component
@Slf4j
public class OffersActivityCallbackType implements CallbackType {

    @Override
    public String getCallbackName() {
        return Callbacks.OFFERS_ACTIVITY.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        List<InlineKeyboardButton> buttons = Arrays.stream(Activity.values())
                .map(activity -> InlineKeyboardButton.builder()
                        .text(activity.name())
                        .callbackData(Callbacks.MY_OFFERS.name() + SPLITTER + activity)
                        .build())
                .collect(Collectors.toList());
        return EditMessageText.builder()
                .text("Choose the role you are interested in")
                .chatId(callbackQuery.getMessage().getChatId().toString())
                .messageId(callbackQuery.getMessage().getMessageId())
                .replyMarkup(InlineKeyboardMarkup.builder()
                        .keyboard(List.of(buttons))
                        .build())
                .build();
    }
}
