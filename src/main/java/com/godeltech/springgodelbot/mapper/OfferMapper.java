package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Offer;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;

public interface OfferMapper {
    Offer mapToOffer(Request request);
    DriverRequest mapToDriverRequest(Offer offer);
    PassengerRequest mapToPassengerRequest(Offer offer);

    ChangeDriverRequest mapToChangeOfferRequest(Offer offer);
}
