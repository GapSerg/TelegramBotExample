package com.godeltech.springgodelbot.resolver.message.type.impl;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.resolver.message.Messages;
import com.godeltech.springgodelbot.resolver.message.type.MessageType;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;

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
        List<String> tokens = tokenService.findByUserId(message.getFrom().getId());
        Map.Entry<String, ? extends Request> entry = requestService.findRequest(tokens, message.getText());
        if (entry == null) {
            return getUnknownMessage(message);
        }
        if (entry.getValue() instanceof DriverRequest) {
            log.info("Got message for saving description of driver with token :{} ", entry.getKey());
            return saveDriverRequest(message, entry);
        }else  if (entry.getValue() instanceof PassengerRequest) {
            log.info("Got message for saving description of passenger with token :{} ", entry.getKey());
            return savePassengerRequest(message, entry);
        }else {
            log.info("Got message for changing description of offer with token ", entry.getKey());
            return updateDescriptionOfRequest(message, entry);
        }
    }

    private SendMessage updateDescriptionOfRequest(Message message, Map.Entry<String, ? extends Request> entry) {
        entry.getValue().setDescription(message.getText());
        requestService.updateDescriptionOfOffer((ChangeOfferRequest) entry.getValue(), entry.getKey() );
        tudaSudaTelegramBot.deleteMessages(message.getChatId(), entry.getValue().getMessages());
        return getStartMenu(message.getChatId(), DESCRIPTION_WAS_UPDATED,tokenService.createToken());
    }

    private SendMessage savePassengerRequest(Message message, Map.Entry<String, ? extends Request> entry) {
        entry.getValue().setDescription(message.getText());
        requestService.savePassenger((PassengerRequest) entry.getValue(), entry.getKey());
        tudaSudaTelegramBot.deleteMessages(message.getChatId(), entry.getValue().getMessages());
        tokenService.deleteToken(entry.getKey());
        return getStartMenu(message.getChatId(), SUCCESSFUL_REQUEST_SAVING,tokenService.createToken());
    }

    private SendMessage saveDriverRequest(Message message, Map.Entry<String, ? extends Request> entry) {
        tudaSudaTelegramBot.deleteMessages(message.getChatId(), entry.getValue().getMessages());
        entry.getValue().setDescription(message.getText());
        requestService.saveDriver((DriverRequest) entry.getValue(), entry.getKey());
        tokenService.deleteToken(entry.getKey());
        return getStartMenu(message.getChatId(), SUCCESSFUL_REQUEST_SAVING,tokenService.createToken());
    }


    private SendMessage getUnknownMessage(Message message) {
        throw new UnknownCommandException(message);
    }
}
