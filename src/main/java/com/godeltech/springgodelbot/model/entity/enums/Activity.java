package com.godeltech.springgodelbot.model.entity.enums;

import lombok.Getter;
import lombok.ToString;

@Getter
public enum Activity {
    DRIVER("Driver \uD83D\uDE99"), PASSENGER("Passenger \uD83D\uDCBA"), PARCEL("Parcel \uD83D\uDCE6");

    @ToString.Exclude
    private String textMessage;

    Activity(String textMessage) {
        this.textMessage = textMessage;
    }
}
