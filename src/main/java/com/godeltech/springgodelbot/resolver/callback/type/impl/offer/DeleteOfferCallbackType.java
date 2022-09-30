package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.MY_OFFERS;
import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.SPLITTER;
import static com.godeltech.springgodelbot.util.ConstantUtil.OFFER_WAS_DELETED;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteOfferCallbackType implements CallbackType {

    private final RequestService requestService;

    @Override
    public java.lang.String getCallbackName() {
        return Callbacks.DELETE_OFFER.name();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        long offerId = Long.parseLong(CallbackUtil.getCallbackValue(callbackQuery.getData()));
        log.info("Delete offer by id :{}", offerId);
        requestService.deleteOffer(callbackQuery.getMessage());
        return getStartMenu(callbackQuery.getMessage(),OFFER_WAS_DELETED);
    }

}
