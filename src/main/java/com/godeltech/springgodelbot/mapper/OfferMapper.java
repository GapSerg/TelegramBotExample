package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.DriverRequest;
import com.godeltech.springgodelbot.model.entity.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.Offer;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface OfferMapper {
    Offer mapToOffer(Request request, User user, List<City> cities);

    DriverRequest mapToDriverRequest(Offer offer);

    PassengerRequest mapToPassengerRequest(Offer offer);

    ChangeOfferRequest mapToChangeOfferRequest(Offer offer);
}
