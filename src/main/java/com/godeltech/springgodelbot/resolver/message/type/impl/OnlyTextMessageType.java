package com.godeltech.springgodelbot.resolver.message.type.impl;

import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.resolver.message.Messages;
import com.godeltech.springgodelbot.resolver.message.type.MessageType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

import static com.godeltech.springgodelbot.resolver.callback.Callbacks.*;
import static com.godeltech.springgodelbot.util.CallbackUtil.showSavedRequestWithDescriptionWithDriverItems;
import static com.godeltech.springgodelbot.util.CallbackUtil.showSavedRequestWithDescriptionWithTransferItems;
import static com.godeltech.springgodelbot.util.ConstantUtil.SUCCESSFUL_REQUEST_SAVING;

@Component
@Slf4j
public class OnlyTextMessageType implements MessageType {

    private final RequestService requestService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;

    public OnlyTextMessageType(RequestService requestService,
                               @Lazy TudaSudaTelegramBot tudaSudaTelegramBot) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
    }

    @Override
    public String getMessageType() {
        return Messages.ONLY_TEXT.name();
    }

    @Override
    public BotApiMethod createSendMessage(Message message) {
        Request request = requestService.findRequestByUserIdForSave(message);
        if (request == null) {
            getUnknownMessage(message);
        }
        if (request instanceof DriverRequest) {
            log.info("Got message for saving description of driver with token :{} ", request.getToken().getId());
            return saveDriverRequest(message, request);
        } else if (request instanceof PassengerRequest) {
            log.info("Got message for saving description of passenger with token :{} ", request.getToken().getId());
            return savePassengerRequest(message, request);
        } else {
            log.info("Got message for changing description of offer with token ", request.getToken().getId());
            return updateDescriptionOfRequest(message, request);
        }
    }

    private SendMessage updateDescriptionOfRequest(Message message, Request request) {
        tudaSudaTelegramBot.deleteMessage(request.getToken().getChatId(), request.getToken().getMessageId());
        request.setDescription(message.getText());
        request.getToken().setMessageId(null);
        requestService.updateDescriptionOfOffer(request, message,message.getFrom());
        if(request.getActivity() == Activity.DRIVER) {
            List<TransferItem> transferItems = requestService.findPassengersByRequestData(request);
            return showSavedRequestWithDescriptionWithTransferItems(message,request,transferItems,CANCEL_CHANGE_OFFER_REQUEST,SUCCESSFUL_REQUEST_SAVING);
        }else {
            List<DriverItem> driverItems = requestService.findDriversByRequestData(request);
            return showSavedRequestWithDescriptionWithDriverItems(message, request, driverItems, CANCEL_CHANGE_OFFER_REQUEST, SUCCESSFUL_REQUEST_SAVING);
        }
    }

    private SendMessage savePassengerRequest(Message message, Request request) {
        tudaSudaTelegramBot.deleteMessage(request.getToken().getChatId(), request.getToken().getMessageId());
        request.setDescription(message.getText());
        request.getToken().setMessageId(null);
        requestService.savePassenger(request, message, message.getFrom());
        List<DriverItem> driverItems = requestService.findDriversByRequestData(request);
        return showSavedRequestWithDescriptionWithDriverItems(message, request, driverItems, CANCEL_PASSENGER_REQUEST, SUCCESSFUL_REQUEST_SAVING);
    }

    private SendMessage saveDriverRequest(Message message, Request request) {
        tudaSudaTelegramBot.deleteMessage(message.getChatId(), request.getToken().getMessageId());
        request.setDescription(message.getText());
        request.getToken().setMessageId(null);
        requestService.saveDriver(request, message, message.getFrom());
        List<TransferItem> transferItems = requestService.findPassengersByRequestData(request);
        return showSavedRequestWithDescriptionWithTransferItems(message, request, transferItems, CANCEL_DRIVER_REQUEST, SUCCESSFUL_REQUEST_SAVING);
    }


    private SendMessage getUnknownMessage(Message message) {
        throw new UnknownCommandException(message);
    }
}
