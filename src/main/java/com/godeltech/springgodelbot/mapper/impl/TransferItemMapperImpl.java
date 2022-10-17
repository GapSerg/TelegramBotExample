package com.godeltech.springgodelbot.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godeltech.springgodelbot.mapper.TransferItemMapper;
import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
@Component
@RequiredArgsConstructor
public class TransferItemMapperImpl implements TransferItemMapper {
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    @Override
    public ChangeOfferRequest mapToChangeOfferRequest(TransferItem transferItem) {
        ChangeOfferRequest changeOfferRequest = objectMapper.convertValue(transferItem, ChangeOfferRequest.class);
        changeOfferRequest.setActivity(transferItem.getActivityType().getName());
        return changeOfferRequest;
    }

    @Override
    public TransferItem mapToTransferItem(Request request, User user, List<City> cities, ActivityType activityType) {
        TransferItem transferItem = objectMapper.convertValue(request, TransferItem.class);
        transferItem.setActivityType(activityType);
        transferItem.setUserEntity(userMapper.mapToUserEntity(user,true));
        transferItem.setCities(cities);
        return transferItem;
    }
}
