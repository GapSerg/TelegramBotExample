package com.godeltech.springgodelbot.model.entity.enums;

import lombok.Data;
import lombok.ToString;

@ToString
public enum RequestType {
    DRIVER_REQUEST, PASSENGER_REQUEST, CHANGE_OF_OFFER_REQUEST, PARCEL_REQUEST
}
