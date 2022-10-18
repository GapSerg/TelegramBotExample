package com.godeltech.springgodelbot.model.entity;

import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.model.entity.enums.RequestType;
import lombok.Builder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;

import static com.godeltech.springgodelbot.model.entity.enums.RequestType.PASSENGER_REQUEST;

@Entity
@DiscriminatorValue("PASSENGER_REQUEST")
public class PassengerRequest extends Request {

    @Builder
    public PassengerRequest(Long id, Long offerId, List<String> cities, LocalDate firstDate,
                            LocalDate secondDate, Boolean needForDescription, Token token,
                            List<Activity> suitableActivities, String description) {
        super(id, offerId, cities, firstDate, secondDate, needForDescription, token, suitableActivities,Activity.PASSENGER, description);
    }


    public PassengerRequest() {
        super(Activity.PASSENGER);
    }
}
