package com.godeltech.springgodelbot.resolver.message.type.impl;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.resolver.message.Messages;
import com.godeltech.springgodelbot.resolver.message.type.MessageType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import static com.godeltech.springgodelbot.util.BotMenu.getStartMenu;
import static com.godeltech.springgodelbot.util.ConstantUtil.DESCRIPTION_WAS_UPDATED;
import static com.godeltech.springgodelbot.util.ConstantUtil.SUCCESSFUL_REQUEST_SAVING;

@Component
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

        if (requestService.existsDriverRequestByChatId(message.getChatId())&& requestService.getDriverRequest(message).getNeedForDescription()) {
            DriverRequest driverRequest = requestService.getDriverRequest(message);

            return driverRequest.getNeedForDescription() ?
                    saveDriverRequestWithDescription(message, driverRequest, message.getText(),
                            SUCCESSFUL_REQUEST_SAVING) :
                    getUnknownMessage(message);
        }
        if (requestService.existsPassengerRequestByChatId(message.getChatId()) && requestService.getPassengerRequest(message).getNeedForDescription()) {
            PassengerRequest passengerRequest = requestService.getPassengerRequest(message);
            return passengerRequest.getNeedForDescription() ?
                    savePassengerRequestWithDescription(message, passengerRequest, "Request was successfully saved") :
                    getUnknownMessage(message);
        }
        if (requestService.existsChangeOfferRequestByChatId(message.getChatId())&& requestService.getChangeOfferRequest(message).getNeedForDescription()) {
            ChangeDriverRequest changeDriverRequest = requestService.getChangeOfferRequest(message);
            return changeDriverRequest.getNeedForDescription() ?
                    updateDescriptionOfOfferAndGetStartMenu(message, changeDriverRequest,
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

    private BotApiMethod updateDescriptionOfOfferAndGetStartMenu(Message message, ChangeDriverRequest changeDriverRequest, String text) {
        tudaSudaTelegramBot.deleteMessages(message.getChatId(), changeDriverRequest.getMessages());
        changeDriverRequest.setDescription(message.getText());
        requestService.updateDescriptionOfOffer(changeDriverRequest);
        return getStartMenu(changeDriverRequest.getChatId(), text);
    }


    private SendMessage getUnknownMessage(Message message) {
        throw new UnknownCommandException(message);
    }
}
