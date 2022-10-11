package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Offer;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.time.LocalDate;
import java.util.List;

public interface OfferService {
    Offer save(DriverRequest driverRequest);

    List<PassengerRequest> findPassengersByFirstDateBeforeAndSecondDateAfterAndCities
            (LocalDate secondDate, LocalDate firstDate, List<City> cities);

    List<DriverRequest> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<City> cities);

    List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity);

    ChangeOfferRequest getById(Long offerId, Message message);

    void deleteById(Long offerId, Message message);

    void updateCities(ChangeOfferRequest changeOfferRequest, Message message);

    void updateDatesOfOffer(ChangeOfferRequest changeOfferRequest, Message message);

    void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest, Message message);

    void deleteBySecondDateAfter(LocalDate now);

    void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date);

    Offer save(PassengerRequest passengerRequest);
}
