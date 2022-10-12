package com.godeltech.springgodelbot.model.entity;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.Token;
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
                         LocalDate secondDate, Boolean needForDescription, Token token, String description) {
        super(id, offerId, cities, firstDate, secondDate, needForDescription, token,
                Activity.DRIVER,  description);
    }

    public DriverRequest() {
        super(Activity.DRIVER);
    }
}
