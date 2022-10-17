package com.godeltech.springgodelbot.mapper.impl;

import com.godeltech.springgodelbot.mapper.DriverItemMapper;
import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.DriverRequest;
import com.godeltech.springgodelbot.model.entity.DriverItem;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
@Component
public class DriverItemMapperImpl implements DriverItemMapper {
    @Override
    public DriverItem mapToOffer(DriverRequest driverRequest, User user, List<City> cities) {
        return null;
    }

    @Override
    public ChangeOfferRequest mapToChangeOfferRequest(DriverItem driverItem) {
        return null;
    }
}
