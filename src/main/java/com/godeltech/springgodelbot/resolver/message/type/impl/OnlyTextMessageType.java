package com.godeltech.springgodelbot.resolver.message.type.impl;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
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

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.ConstantUtil.DESCRIPTION_WAS_UPDATED;
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

        if (requestService.existsDriverRequestByChatId(message.getChatId()) && requestService.getDriverRequest(message).getNeedForDescription()) {

            DriverRequest driverRequest = requestService.getDriverRequest(message);
            log.info("Got message for saving description of driver with chat id :{} ",driverRequest.getChatId());
            return driverRequest.getNeedForDescription() ?
                    saveDriverRequestWithDescription(message, driverRequest, message.getText(),
                            SUCCESSFUL_REQUEST_SAVING) :
                    getUnknownMessage(message);
        }
        if (requestService.existsPassengerRequestByChatId(message.getChatId()) && requestService.getPassengerRequest(message).getNeedForDescription()) {
            PassengerRequest passengerRequest = requestService.getPassengerRequest(message);
            log.info("Got message for saving description of passenger with chat id :{} ",passengerRequest.getChatId());
            return passengerRequest.getNeedForDescription() ?
                    savePassengerRequestWithDescription(message, passengerRequest, "Request was successfully saved") :
                    getUnknownMessage(message);
        }
        if (requestService.existsChangeOfferRequestByChatId(message.getChatId()) && requestService.getChangeOfferRequest(message).getNeedForDescription()) {
            ChangeOfferRequest changeOfferRequest = requestService.getChangeOfferRequest(message);
            log.info("Got message for changing description of offer with id :{} ", changeOfferRequest.getOfferId());
            return changeOfferRequest.getNeedForDescription() ?
                    updateDescriptionOfOfferAndGetStartMenu(message, changeOfferRequest,
                            DESCRIPTION_WAS_UPDATED) :
                    getUnknownMessage(message);
        }

        return getUnknownMessage(message);
    }

    private BotApiMethod savePassengerRequestWithDescription(Message message, PassengerRequest passengerRequest,
                                                             String text) {
        passengerRequest.setDescription(message.getText());
        requestService.savePassenger(passengerRequest);

        tudaSudaTelegramBot.deleteMessages(message.getChatId(), passengerRequest.getMessages());
        return getStartMenu(message.getChatId(), text);

    }

    private BotApiMethod saveDriverRequestWithDescription(Message message, DriverRequest driverRequest, String description, String text) {
        tudaSudaTelegramBot.deleteMessages(message.getChatId(), driverRequest.getMessages());
        driverRequest.setDescription(description);
        requestService.saveDriver(message);
        return getStartMenu(message.getChatId(), text);
    }

    private BotApiMethod updateDescriptionOfOfferAndGetStartMenu(Message message, ChangeOfferRequest changeOfferRequest, String text) {
        tudaSudaTelegramBot.deleteMessages(message.getChatId(), changeOfferRequest.getMessages());
        changeOfferRequest.setDescription(message.getText());
        requestService.updateDescriptionOfOffer(changeOfferRequest);
        return getStartMenu(changeOfferRequest.getChatId(), text);
    }


    private SendMessage getUnknownMessage(Message message) {
        throw new UnknownCommandException(message);
    }
}
