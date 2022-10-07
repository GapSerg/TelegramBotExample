package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.util.CallbackUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.CallbackUtil.getCallbackToken;
import static com.godeltech.springgodelbot.util.ConstantUtil.OFFER_WAS_DELETED;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeleteOfferCallbackType implements CallbackType {

    private final RequestService requestService;
    private final TokenService tokenService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.DELETE_OFFER.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        long offerId = Long.parseLong(CallbackUtil.getCallbackValue(callbackQuery.getData()));
        log.info("Delete offer by id :{} and token: {}", offerId,token);
        requestService.deleteOffer(callbackQuery.getMessage(), token);
        tokenService.deleteToken(token);
        return getStartMenu(callbackQuery.getMessage(), OFFER_WAS_DELETED,tokenService.createToken());
    }

}
