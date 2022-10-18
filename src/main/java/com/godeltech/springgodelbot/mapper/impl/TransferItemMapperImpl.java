package com.godeltech.springgodelbot.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godeltech.springgodelbot.mapper.TransferItemMapper;
import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TransferItemMapperImpl implements TransferItemMapper {
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;
    @Override
    public ChangeOfferRequest mapToChangeOfferRequest(TransferItem transferItem) {
        return ChangeOfferRequest.builder()
                .offerId(transferItem.getId())
                .activity(transferItem.getActivityType().getName())
                .description(transferItem.getDescription())
                .firstDate(transferItem.getFirstDate())
                .secondDate(transferItem.getSecondDate())
                .cities(transferItem.getCities().stream()
                        .map(City::getName)
                        .collect(Collectors.toList()))
                .build();
    }

    @Override
    public TransferItem mapToTransferItem(Request request, User user, List<City> cities, ActivityType activityType) {
        return TransferItem.builder()
                .firstDate(request
                        .getFirstDate())
                .secondDate(request.getSecondDate())
                .activityType(activityType)
                .cities(cities)
                .userEntity(userMapper.mapToUserEntity(user, true))
                .description(request.getDescription())
                .build();
    }
}
