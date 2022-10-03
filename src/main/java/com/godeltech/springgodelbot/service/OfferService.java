package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Offer;

import java.time.LocalDate;
import java.util.List;

public interface OfferService {
    Offer save(DriverRequest driverRequest);

    List<PassengerRequest> findPassengersByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<City> cities);

    List<DriverRequest> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<City> cities);

    List<ChangeDriverRequest> findByUserEntityIdAndActivity(Long id, Activity activity);

    ChangeDriverRequest getById(Long offerId, Long chatId);

    void deleteById(Long offerId, Long chatId);

    void updateRoute(ChangeDriverRequest changeDriverRequest);

    void updateDatesOfOffer(ChangeDriverRequest changeDriverRequest);

    void updateDescriptionOfOffer(ChangeDriverRequest changeDriverRequest);

    void deleteBySecondDateAfter(LocalDate now);

    Offer save(PassengerRequest passengerRequest);
}
