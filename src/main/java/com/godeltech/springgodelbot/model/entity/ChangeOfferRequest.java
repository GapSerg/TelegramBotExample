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
@DiscriminatorValue("CHANGE_OFFER_REQUEST")
public class ChangeOfferRequest extends Request {

    @Builder
    public ChangeOfferRequest(Long id, Long offerId, List<String> cities, LocalDate firstDate, LocalDate secondDate,
                              Boolean needForDescription, Token token, Activity activity, String description) {
        super(id, offerId, cities, firstDate, secondDate, needForDescription, token, activity,
                 description);
    }


    public ChangeOfferRequest() {
    }
}
