package com.godeltech.springgodelbot.model.entity;

import com.godeltech.springgodelbot.model.entity.enums.Activity;
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
                         List<Activity> suitableActivities,
                         String description) {
        super(id, offerId, cities, firstDate, secondDate, needForDescription, token, suitableActivities, Activity.DRIVER, description);
    }




    public DriverRequest() {
        super(Activity.DRIVER);
    }
}
