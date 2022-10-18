package com.godeltech.springgodelbot.model.entity;

import com.godeltech.springgodelbot.model.entity.enums.Activity;
import lombok.Builder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDate;
import java.util.List;
@Entity
@DiscriminatorValue("CHANGE_OFFER_REQUEST")
public class ChangeOfferRequest extends Request {
    @Builder
    public ChangeOfferRequest(Long id, Long offerId, List<String> cities, LocalDate firstDate,
                              LocalDate secondDate, Boolean needForDescription,
                              Token token, List<Activity> suitableActivities, Activity activity, String description) {
        super(id, offerId, cities, firstDate, secondDate, needForDescription,
                token, suitableActivities, activity, description);
    }





    public ChangeOfferRequest() {
    }
}
