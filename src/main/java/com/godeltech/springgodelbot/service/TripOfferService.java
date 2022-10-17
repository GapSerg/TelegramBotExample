package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;

public interface TripOfferService {
    TripOffer save(DriverRequest driverRequest, User user, Message message);

    List<TripOffer> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<String> cities);

    List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity, Message message, User user);

    ChangeOfferRequest getById(Long offerId, Message message, User user);

    void deleteById(Long offerId, Message message, User user);

    void updateCities(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDatesOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDescriptionOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void deleteBySecondDateAfter(LocalDate now);

    void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date);
}
