package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Map;

public interface RequestService {

    void saveDriverRequest(DriverRequest driverRequest, String token);

    DriverRequest getDriverRequest(Message message, String token);

    void savePassengerRequest(PassengerRequest passengerRequest, String token);

    void saveDriver(DriverRequest driverRequest, String token);

    PassengerRequest getPassengerRequest(Message message, String token);

    ChangeOfferRequest getChangeOfferRequest(Message message, String token);

    void updateDates(ChangeOfferRequest changeOfferRequest, String token);

    void clearChangeOfferRequestsAndPassengerRequests(String token);

    void clearDriverRequestsAndPassengerRequests(String token);

    void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest, String token);

    void deleteChangeOfferRequest(String token);
    void deleteDriverRequest(String token);
    void deletePassengerRequest(String token);

    void clearDriverRequestsAndChangeOfferRequests(String token);

    void savePassenger(PassengerRequest passengerRequest, String token);

    boolean existsPassengerRequestByToken(String token);

    void updateRouteOfOffer(ChangeOfferRequest changeOfferRequest, String token);

    ChangeOfferRequest deleteOffer(Message message, String token);

    void checkAndClearChangingOfferRequests(String token);

    List<ChangeOfferRequest> findByUserIdAndActivity(Long id, Activity activity);

    ChangeOfferRequest addNewChangeOfferRequest(long offerId, Long chatId, String token);

    List<PassengerRequest> findPassengersByRequestData(Request request);

    List<DriverRequest> findDriversByRequestData(Request request);

    Map.Entry<String, ? extends Request> findRequest(List<String> tokens, String text);
}
