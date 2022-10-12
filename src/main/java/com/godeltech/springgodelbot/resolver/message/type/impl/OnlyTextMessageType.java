package com.godeltech.springgodelbot.resolver.message.type.impl;

import com.godeltech.springgodelbot.model.entity.DriverRequest;
import com.godeltech.springgodelbot.model.entity.PassengerRequest;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.resolver.message.Messages;
import com.godeltech.springgodelbot.resolver.message.type.MessageType;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.TokenService;
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
    private final TokenService tokenService;

    public OnlyTextMessageType(RequestService requestService,
                               @Lazy TudaSudaTelegramBot tudaSudaTelegramBot, TokenService tokenService) {
        this.requestService = requestService;
        this.tudaSudaTelegramBot = tudaSudaTelegramBot;
        this.tokenService = tokenService;
    }

    @Override
    public String getMessageType() {
        return Messages.ONLY_TEXT.name();
    }

    @Override
    public BotApiMethod createSendMessage(Message message) {
//        List<String> tokens = tokenService.findByUserId(message.getFrom().getId());
        tudaSudaTelegramBot.checkMembership(message);
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

    private SendMessage updateDescriptionOfRequest(Message message,Request request) {
        request.setDescription(message.getText());
        requestService.updateDescriptionOfOffer( request, message);
        tudaSudaTelegramBot.deleteMessage(request.getToken().getChatId(), request.getToken().getMessageId());
        Token createdToken = tokenService.createToken(message.getFrom().getId(), message.getChatId());
        return getStartMenu(message.getChatId(), DESCRIPTION_WAS_UPDATED, createdToken.getId());
    }

    private SendMessage savePassengerRequest(Message message, Request request) {
        request.setDescription(message.getText());
        requestService.savePassenger(request, message, message.getFrom());
        tudaSudaTelegramBot.deleteMessage(request.getToken().getChatId(), request.getToken().getMessageId());
        Token createdToken = tokenService.createToken(message.getFrom().getId(),
                message.getChatId());
        return getStartMenu(message.getChatId(), SUCCESSFUL_REQUEST_SAVING, createdToken.getId());
    }

    private SendMessage saveDriverRequest(Message message, Request request) {
        tudaSudaTelegramBot.deleteMessage(message.getChatId(), request.getToken().getMessageId());
        request.setDescription(message.getText());
        requestService.saveDriver(request, message,message.getFrom() );
        Token createdToken = tokenService.createToken(message.getFrom().getId(),
                message.getChatId());
        return getStartMenu(message.getChatId(), SUCCESSFUL_REQUEST_SAVING, createdToken.getId());
    }


    private SendMessage getUnknownMessage(Message message) {
        throw new UnknownCommandException(message);
    }
}
