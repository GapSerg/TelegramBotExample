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

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {


    private final Map<Long, PassengerRequest> passengerRequests;

    private final Map<Long, DriverRequest> driverRequests;

    private final Map<Long, ChangeOfferRequest> changeDriverRequests;

    private final OfferService offerService;


    public DriverRequest getDriverRequest(Message message) {
        log.debug("Getting driver request with chatId: {}", message.getChatId());
        if (driverRequests.containsKey(message.getChatId()))
            return driverRequests.get(message.getChatId());
        throw new RequestNotFoundException(DriverRequest.class, "chatId", message.getChatId(), message);
    }

    @Override
    public void savePassengerRequest(PassengerRequest passengerRequest) {
        log.debug("Save passenger request : {}",passengerRequest);
        passengerRequests.put(passengerRequest.getChatId(), passengerRequest);
    }

    @Override
    public void saveDriver(Message message) {
        log.debug("Saving driver request with chat id: {}", message.getChatId());
        if (driverRequests.containsKey(message.getChatId())) {
            offerService.save(driverRequests.get(message.getChatId()));
            driverRequests.remove(message.getChatId());
        } else {
            throw new RequestNotFoundException(DriverRequest.class, "chatId", message.getChatId(),
                    message);
        }
    }

    @Override
    public PassengerRequest getPassengerRequest(Message message) {
        log.debug("Get passenger request with chat id: {}", message.getChatId());
        if (passengerRequests.containsKey(message.getChatId()))
            return passengerRequests.get(message.getChatId());
        throw new RequestNotFoundException(PassengerRequest.class, "chatId", message.getChatId(), message);
    }


    @Override
    public ChangeOfferRequest getChangeOfferRequest(Message message) {
        log.debug("Get change offer request by chat id: {}", message.getChatId());
        if (changeDriverRequests.containsKey(message.getChatId()))
            return changeDriverRequests.get(message.getChatId());
        throw new RequestNotFoundException(ChangeOfferRequest.class, "chatId", message.getChatId(), message);
    }

    @Override
    public void updateDates(ChangeOfferRequest changeOfferRequest) {
        log.debug("Update dates of offer with id: {}", changeOfferRequest.getOfferId());
        offerService.updateDatesOfOffer(changeOfferRequest);
        changeDriverRequests.remove(changeOfferRequest.getChatId());
    }

    @Override
    public void clearChangeOfferRequestsAndPassengerRequests(Long chatId) {
        log.debug("Clear maps changeOfferRequests and passengerRequests with chat id: {}", chatId);
        changeDriverRequests.remove(chatId);
        passengerRequests.remove(chatId);
    }

    @Override
    public void clearDriverRequestsAndPassengerRequests(Long chatId) {
        log.debug("Clear driver and passenger requests with chat id :{}", chatId);
        driverRequests.remove(chatId);
        passengerRequests.remove(chatId);
    }

    @Override
    public void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest) {
        log.debug("Update description of offer with offer id: {}", changeOfferRequest.getOfferId());
        offerService.updateDescriptionOfOffer(changeOfferRequest);
        changeDriverRequests.remove(changeOfferRequest.getChatId());
    }

    @Override
    public boolean existsDriverRequestByChatId(Long chatId) {
        log.debug("Check containing driverRequests by id: {}", chatId);
        return driverRequests.containsKey(chatId);
    }

    @Override
    public boolean existsChangeOfferRequestByChatId(Long chatId) {
        log.debug("Check containing changeDriverRequests by id: {}", chatId);
        return changeDriverRequests.containsKey(chatId);
    }

    @Override
    public void clearDriverRequestsAndChangeOfferRequests(Long chatId) {
        log.debug("Clear driver and change offer requests with chat id :{}", chatId);
        driverRequests.remove(chatId);
        changeDriverRequests.remove(chatId);
    }

    @Override
    public void savePassenger(PassengerRequest passengerRequest) {
        log.debug("Save passenger request : {}",passengerRequest);
        Long chatId = passengerRequest.getChatId();
        offerService.save(passengerRequest);
        passengerRequests.remove(chatId);
    }

    @Override
    public boolean existsPassengerRequestByChatId(Long chatId) {
        return passengerRequests.containsKey(chatId);
    }

    @Override
    public void updateRouteOfOffer(ChangeOfferRequest changeOfferRequest) {
        log.info("Update route of offer with id :{}", changeOfferRequest.getOfferId());
        Long chatId = changeOfferRequest.getChatId();
        offerService.updateCities(changeOfferRequest);
        changeDriverRequests.remove(chatId);
    }

    @Override
    public ChangeOfferRequest deleteOffer(Message message) {
        ChangeOfferRequest changeOfferRequest = getChangeOfferRequest(message);
        log.debug("Delete offer with id : {}", changeOfferRequest.getOfferId());
        offerService.deleteById(changeOfferRequest.getOfferId(), message.getChatId());
        changeDriverRequests.remove(message.getChatId());
        return changeOfferRequest;

    }

    @Override
    public void checkAndClearChangingOfferRequests(Long chatId) {
        log.debug("Check and clear if exists changeSupplierRequests by key:{}", chatId);
        changeDriverRequests.remove(chatId);
    }

    @Override
    public List<ChangeOfferRequest> findByUserIdAndActivity(Long id, Activity activity) {
        log.debug("Find offers by id:{} and activity :{}", id, activity);
        return offerService.findByUserEntityIdAndActivity(id, activity);
    }

    @Override
    public ChangeOfferRequest addNewChangeOfferRequest(long offerId, Long chatId) {
        log.debug("Add new change offer request with offer id : {}",offerId);
        ChangeOfferRequest request = offerService.getById(offerId, chatId);
        request.setChatId(chatId);
        changeDriverRequests.put(chatId, request);
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


    public void saveDriverRequest(DriverRequest driverRequest) {
        log.debug("Save supplier request: {}", driverRequest);
        driverRequests.put(driverRequest.getChatId(), driverRequest);

    }
}
