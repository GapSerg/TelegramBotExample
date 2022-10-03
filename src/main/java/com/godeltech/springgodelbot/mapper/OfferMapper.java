package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Offer;

public interface OfferMapper {
    Offer mapToOffer(Request request);

    DriverRequest mapToDriverRequest(Offer offer);

    PassengerRequest mapToPassengerRequest(Offer offer);

    ChangeOfferRequest mapToChangeOfferRequest(Offer offer);
}
