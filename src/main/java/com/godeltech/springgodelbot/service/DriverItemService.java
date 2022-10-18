package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;

public interface DriverItemService {
    DriverItem save(DriverRequest driverRequest, User user, Message message);

    List<DriverItem> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<String> cities, Activity activity);

    List<ChangeOfferRequest> findByUserEntityId(Long id, Message message, User user);

    ChangeOfferRequest getById(Long offerId, Message message, User user);

    void deleteById(Long offerId, Message message, User user);

    void updateCitiesOfDriverItem(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDatesOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void updateDescriptionOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user);

    void deleteBySecondDateAfter(LocalDate now);

    void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date);
}
