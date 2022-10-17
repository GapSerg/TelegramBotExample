package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.DriverRequest;
import com.godeltech.springgodelbot.model.entity.DriverItem;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface DriverItemMapper {
    DriverItem mapToOffer(DriverRequest driverRequest, User user, List<City> cities);

    ChangeOfferRequest mapToChangeOfferRequest(DriverItem driverItem);
}
