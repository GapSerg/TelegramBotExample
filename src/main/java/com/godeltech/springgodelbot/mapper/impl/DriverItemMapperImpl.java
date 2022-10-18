package com.godeltech.springgodelbot.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godeltech.springgodelbot.mapper.DriverItemMapper;
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
public class DriverItemMapperImpl implements DriverItemMapper {
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    @Override
    public DriverItem mapToOffer(DriverRequest driverRequest, User user, List<City> cities
            , List<ActivityType> suitableActivities) {
        return DriverItem.builder()
                .firstDate(driverRequest.getFirstDate())
                .secondDate(driverRequest.getSecondDate())
                .suitableActivities(suitableActivities)
                .cities(cities)
                .userEntity(userMapper.mapToUserEntity(user, true))
                .description(driverRequest.getDescription())
                .build();
    }

    @Override
    public ChangeOfferRequest mapToChangeOfferRequest(DriverItem driverItem) {
        return ChangeOfferRequest.builder()
                .offerId(driverItem.getId())
                .activity(Activity.DRIVER)
                .description(driverItem.getDescription())
                .firstDate(driverItem.getFirstDate())
                .secondDate(driverItem.getSecondDate())
                .cities(driverItem.getCities().stream()
                        .map(City::getName)
                        .collect(Collectors.toList()))
                .suitableActivities(driverItem.getSuitableActivities().stream()
                        .map(ActivityType::getName)
                        .collect(Collectors.toList()))
                .build();
    }
}
