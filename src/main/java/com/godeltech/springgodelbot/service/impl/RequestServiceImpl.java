package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.exception.RequestNotFoundException;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.service.OfferService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.*;
import java.util.stream.Stream;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {


    private final Map<String, PassengerRequest> passengerRequests;

    private final Map<String, DriverRequest> driverRequests;

    private final Map<String, ChangeOfferRequest> changeDriverRequests;

    private final OfferService offerService;


    public DriverRequest getDriverRequest(Message message, String token) {
        log.debug("Getting driver request with token {}", token);
        if (driverRequests.containsKey(token))
            return driverRequests.get(token);
        throw new RequestNotFoundException(DriverRequest.class, "token", token, message);
    }

    @Override
    public void savePassengerRequest(PassengerRequest passengerRequest, String token) {
        log.debug("Save passenger request : {}", passengerRequest);
        passengerRequests.put(token, passengerRequest);
    }

    @Override
    public void saveDriver(DriverRequest driverRequest, String token) {
        log.debug("Saving driver request with token: {}", token);
            offerService.save(driverRequest);
            driverRequests.remove(token);

    }

    @Override
    public PassengerRequest getPassengerRequest(Message message, String token) {
        log.debug("Get passenger request with chat token: {}", token);
        if (passengerRequests.containsKey(token))
            return passengerRequests.get(token);
        throw new RequestNotFoundException(PassengerRequest.class, "token", token, message);
    }


    @Override
    public ChangeOfferRequest getChangeOfferRequest(Message message, String token) {
        log.debug("Get change offer request by token: {}", token);
        if (changeDriverRequests.containsKey(token))
            return changeDriverRequests.get(token);
        throw new RequestNotFoundException(ChangeOfferRequest.class, "token", token, message);
    }

    @Override
    public void updateDates(ChangeOfferRequest changeOfferRequest, String token) {
        log.debug("Update dates of offer with id: {} and token: {}", changeOfferRequest.getOfferId(), token);
        offerService.updateDatesOfOffer(changeOfferRequest);
        changeDriverRequests.remove(token);
    }

    @Override
    public void clearChangeOfferRequestsAndPassengerRequests(String token) {
        log.debug("Clear maps changeOfferRequests and passengerRequests with chat id: {}", token);
        changeDriverRequests.remove(token);
        passengerRequests.remove(token);
    }

    @Override
    public void clearDriverRequestsAndPassengerRequests(String token) {
        log.debug("Clear driver and passenger requests with chat id :{}", token);
        driverRequests.remove(token);
        passengerRequests.remove(token);
    }

    @Override
    public void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest, String token) {
        log.debug("Update description of offer with offer id: {} and token: {}", changeOfferRequest.getOfferId(), token);
        offerService.updateDescriptionOfOffer(changeOfferRequest);
        changeDriverRequests.remove(token);
    }

    @Override
    public boolean existsDriverRequestByChatId(String token) {
        log.debug("Check containing driverRequests by token: {}", token);
        return driverRequests.containsKey(token);
    }

    @Override
    public boolean existsChangeOfferRequestByChatId(String token) {
        log.debug("Check containing changeDriverRequests by token: {}", token);
        return changeDriverRequests.containsKey(token);
    }

    @Override
    public void clearDriverRequestsAndChangeOfferRequests(String token) {
        log.debug("Clear driver and change offer requests with chat id :{}", token);
        driverRequests.remove(token);
        changeDriverRequests.remove(token);
    }

    @Override
    public void savePassenger(PassengerRequest passengerRequest, String token) {
        log.debug("Save passenger request : {} and token: {}", passengerRequest, token);
        offerService.save(passengerRequest);
        passengerRequests.remove(token);
    }

    @Override
    public boolean existsPassengerRequestByToken(String token) {
        return passengerRequests.containsKey(token);
    }

    @Override
    public void updateRouteOfOffer(ChangeOfferRequest changeOfferRequest, String token) {
        log.info("Update route of offer with id :{}", changeOfferRequest.getOfferId());
        offerService.updateCities(changeOfferRequest);
        changeDriverRequests.remove(token);
    }

    @Override
    public ChangeOfferRequest deleteOffer(Message message, String token) {
        ChangeOfferRequest changeOfferRequest = getChangeOfferRequest(message, token);
        log.debug("Delete offer with id : {} and token: {}", changeOfferRequest.getOfferId(), token);
        offerService.deleteById(changeOfferRequest.getOfferId(), message.getChatId());
        changeDriverRequests.remove(token);
        return changeOfferRequest;

    }

    @Override
    public void checkAndClearChangingOfferRequests(String token) {
        log.debug("Check and clear if exists changeSupplierRequests by token:{}", token);
        changeDriverRequests.remove(token);
    }

    @Override
    public List<ChangeOfferRequest> findByUserIdAndActivity(Long id, Activity activity) {
        log.debug("Find offers by id:{} and activity :{}", id, activity);
        return offerService.findByUserEntityIdAndActivity(id, activity);
    }

    @Override
    public ChangeOfferRequest addNewChangeOfferRequest(long offerId, Long chatId, String token) {
        log.debug("Add new change offer request with offer id : {} and token: {}", offerId, token);
        ChangeOfferRequest request = offerService.getById(offerId, chatId);
        request.setChatId(chatId);
        changeDriverRequests.put(token, request);
        return request;
    }

    @Override
    public List<PassengerRequest> findPassengersByRequestData(Request request) {
        log.debug("Find passengers by secondDate:{},firstDate:{},routes:{}", request.getSecondDate(),
                request.getFirstDate(), request.getCities());
        return offerService.findPassengersByFirstDateBeforeAndSecondDateAfterAndCities(request.getSecondDate(),
                request.getFirstDate(), request.getCities());
    }

    @Override
    public List<DriverRequest> findDriversByRequestData(Request request) {
        log.debug("Find drivers by secondDate:{},firstDate:{},routes:{}", request.getSecondDate(),
                request.getFirstDate(), request.getCities());
        return offerService.findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes(request.getSecondDate(),
                request.getFirstDate(), request.getCities());
    }

    @Override
    public Map.Entry<String, ? extends Request> findRequest(List<String> tokens, String text) {
        return Stream.of(driverRequests.entrySet(), passengerRequests.entrySet(), changeDriverRequests.entrySet())
                .flatMap(Set::stream)
                .filter(request -> tokens.contains(request.getKey()))
                .filter(request -> request.getValue().getNeedForDescription())
                .findAny()
                .orElse(null);
    }


    public void saveDriverRequest(DriverRequest driverRequest, String token) {
        log.debug("Save supplier request: {} with token : {}", driverRequest, token);
        driverRequests.put(token, driverRequest);

    }
}
