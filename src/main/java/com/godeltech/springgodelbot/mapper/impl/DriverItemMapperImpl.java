package com.godeltech.springgodelbot.mapper.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godeltech.springgodelbot.mapper.DriverItemMapper;
import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.model.entity.*;
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
        DriverItem driverItem= objectMapper.convertValue(driverRequest,DriverItem.class);
        driverItem.setSuitableActivities(suitableActivities);
        driverItem.setCities(cities);
        driverItem.setUserEntity(userMapper.mapToUserEntity(user,true));
        return driverItem;
    }

    @Override
    public ChangeOfferRequest mapToChangeOfferRequest(DriverItem driverItem) {
        ChangeOfferRequest changeOfferRequest = objectMapper.convertValue(driverItem,ChangeOfferRequest.class);
        List<Activity> activities = driverItem.getSuitableActivities().stream()
                .map(ActivityType::getName)
                .collect(Collectors.toList());
        changeOfferRequest.setSuitableActivities(activities);
        changeOfferRequest.setActivity(Activity.DRIVER);
        return changeOfferRequest;
    }
}
