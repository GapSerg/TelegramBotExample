package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.DriverRequest;
import com.godeltech.springgodelbot.model.entity.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.Offer;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;

public interface OfferService {
    Offer save(DriverRequest driverRequest, User user, Message message);

    List<Offer> findPassengersByFirstDateBeforeAndSecondDateAfterAndCities
            (LocalDate secondDate, LocalDate firstDate, List<String> cities);

    List<Offer> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<String> cities);

    List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity);

    ChangeOfferRequest getById(Long offerId, Message message, User user);

    void deleteById(Long offerId, Message message, User user);

    void updateCities(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDatesOfOffer(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void deleteBySecondDateAfter(LocalDate now);

    void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date);

    Offer save(PassengerRequest passengerRequest, User user, Message message);
}
