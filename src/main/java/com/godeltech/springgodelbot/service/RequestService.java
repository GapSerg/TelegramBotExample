package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public interface RequestService {

    void saveDriverRequest(DriverRequest driverRequest);

    DriverRequest getDriverRequest(Message message);

    void savePassengerRequest(PassengerRequest passengerRequest);

    void saveDriver(Message message);

    PassengerRequest getPassengerRequest(Message message);

    ChangeDriverRequest getChangeOfferRequest(Message message);

    void updateDates(ChangeDriverRequest changeDriverRequest);

    void clearChangeOfferRequestsAndPassengerRequests(Long chatId);

    void clearDriverRequestsAndPassengerRequests(Long chatId);

    void updateDescriptionOfOffer(ChangeDriverRequest changeDriverRequest);

    boolean existsDriverRequestByChatId(Long chatId);

    boolean existsChangeOfferRequestByChatId(Long chatId);

    void clearDriverRequestsAndChangeOfferRequests(Long chatId);

    void savePassenger(PassengerRequest passengerRequest);

    boolean existsPassengerRequestByChatId(Long chatId);

    void updateRouteOfOffer(ChangeDriverRequest changeDriverRequest);

    ChangeDriverRequest deleteOffer(Message message);

    void checkAndClearChangingOfferRequests(Long chatId);

    List<ChangeDriverRequest> findByUserIdAndActivity(Long id, Activity activity);

    ChangeDriverRequest addNewChangeOfferRequest(long offerId, Long chatId);

    List<PassengerRequest> findPassengersByRequestData(Request request);

    List<DriverRequest> findDriversByRequestData(Request request);
}
