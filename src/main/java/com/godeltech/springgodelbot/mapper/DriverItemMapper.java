package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.model.entity.*;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface DriverItemMapper {
    DriverItem mapToOffer(DriverRequest driverRequest, User user, List<City> cities, List<ActivityType> suitableActivities);

    ChangeOfferRequest mapToChangeOfferRequest(DriverItem driverItem);
}
