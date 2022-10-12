package com.godeltech.springgodelbot.model.entity;

import lombok.Builder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;
@Entity
@DiscriminatorValue("PASSENGER_REQUEST")
public class PassengerRequest extends Request {
    @Builder
    public PassengerRequest(Long id, Long offerId, List<String> cities, LocalDate firstDate, LocalDate secondDate, Boolean needForDescription, Token token, String description) {
        super(id, offerId, cities, firstDate, secondDate, needForDescription, token, Activity.PASSENGER, description);
    }

    public PassengerRequest() {
        super(Activity.PASSENGER);
    }
}
