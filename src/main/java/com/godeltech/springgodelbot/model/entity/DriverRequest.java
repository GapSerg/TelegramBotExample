package com.godeltech.springgodelbot.model.entity;

import lombok.Builder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;
@Entity
@DiscriminatorValue("DRIVER_REQUEST")
public class DriverRequest extends Request {
    @Builder
    public DriverRequest(Long id, Long offerId, List<String> cities, LocalDate firstDate,
                         LocalDate secondDate, Boolean needForDescription, Token token,
                         List<Activity> suitableActivities, Activity activity,
                         String description) {
        super(id, offerId, cities, firstDate, secondDate, needForDescription, token, suitableActivities, activity, description);
    }




    public DriverRequest() {
        super(Activity.DRIVER);
    }
}
