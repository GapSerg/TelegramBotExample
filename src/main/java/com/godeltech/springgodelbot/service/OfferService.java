package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Offer;

import java.time.LocalDate;
import java.util.List;

public interface OfferService {
    Offer save(DriverRequest driverRequest);

    List<PassengerRequest> findPassengersByFirstDateBeforeAndSecondDateAfterAndCities
            (LocalDate secondDate, LocalDate firstDate, List<City> cities);

    List<DriverRequest> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<City> cities);

    List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity);

    ChangeOfferRequest getById(Long offerId, Long chatId);

    void deleteById(Long offerId, Long chatId);

    void updateCities(ChangeOfferRequest changeOfferRequest);

    void updateDatesOfOffer(ChangeOfferRequest changeOfferRequest);

    void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest);

    void deleteBySecondDateAfter(LocalDate now);

    Offer save(PassengerRequest passengerRequest);
}
