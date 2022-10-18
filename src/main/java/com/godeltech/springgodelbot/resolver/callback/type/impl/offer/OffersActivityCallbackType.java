package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.UserService;
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

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.OFFERS_ACTIVITY;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.SPLITTER;

@Component
@Slf4j
public class OffersActivityCallbackType implements CallbackType {
    private final TokenService tokenService;
    private final UserService userService;

    public OffersActivityCallbackType(TokenService tokenService, UserService userService) {
        this.tokenService = tokenService;
        this.userService = userService;
    }

    @Override
    public Integer getCallbackName() {
        return Callbacks.OFFERS_ACTIVITY.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Got {} callback type with token : {} by user : {}",
                OFFERS_ACTIVITY, token, callbackQuery.getFrom().getUserName());
        tokenService.checkIncomeToken(token, callbackQuery.getMessage(), callbackQuery.getFrom());
        userService.userAuthorization(callbackQuery.getFrom(), callbackQuery.getMessage(), false);
        List<InlineKeyboardButton> buttons = Arrays.stream(Activity.values())
                .map(activity -> InlineKeyboardButton.builder()
                        .text(activity.name())
                        .callbackData(Callbacks.MY_OFFERS.ordinal() + SPLITTER + token + SPLITTER + activity)
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
