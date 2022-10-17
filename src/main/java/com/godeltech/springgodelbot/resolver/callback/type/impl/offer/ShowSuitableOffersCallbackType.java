package com.godeltech.springgodelbot.resolver.callback.type.impl.offer;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.DriverItem;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.TransferItem;
import com.godeltech.springgodelbot.resolver.callback.Callbacks;
import com.godeltech.springgodelbot.resolver.callback.type.CallbackType;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.CHANGE_OFFER;
import static com.godeltech.springgodelbot.util.CallbackUtil.*;
import static com.godeltech.springgodelbot.util.ConstantUtil.SPLITTER;

@Component
@RequiredArgsConstructor
@Slf4j
public class ShowSuitableOffersCallbackType implements CallbackType {
    private final RequestService requestService;

    @Override
    public Integer getCallbackName() {
        return Callbacks.SHOW_SUITABLE_OFFERS.ordinal();
    }

    @Override
    public BotApiMethod createSendMessage(CallbackQuery callbackQuery) {
        String token = getCallbackToken(callbackQuery.getData());
        log.info("Get callback : {} type with token : {} by user : {}",
                Callbacks.SHOW_SUITABLE_OFFERS, token, callbackQuery.getFrom().getUserName());
        Request changeOfferRequest = requestService.getRequest(callbackQuery.getMessage(), token, callbackQuery.getFrom());
        if(changeOfferRequest.getActivity() == Activity.DRIVER){
            List<TransferItem> requests = requestService.findPassengersByRequestData(changeOfferRequest);
            return EditMessageText.builder()
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text(getCompletedMessageAnswerWithTransferItems(requests, changeOfferRequest, ""))
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(List.of(List.of(
                                    InlineKeyboardButton.builder()
                                            .text("Back")
                                            .callbackData(CHANGE_OFFER.ordinal() + SPLITTER + token+SPLITTER+changeOfferRequest.getOfferId())
                                            .build()
                            )))
                            .build())
                    .build();
        }else {
            List<DriverItem> requests =requestService.findDriversByRequestData(changeOfferRequest);
            return EditMessageText.builder()
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text(getCompletedMessageAnswerWithDriverItems(requests, changeOfferRequest, ""))
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboard(List.of(List.of(
                                    InlineKeyboardButton.builder()
                                            .text("Back")
                                            .callbackData(CHANGE_OFFER.ordinal() + SPLITTER + token+SPLITTER+changeOfferRequest.getOfferId())
                                            .build()
                            )))
                            .build())
                    .build();
        }

    }
}
