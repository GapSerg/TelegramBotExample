package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.dto.Request;
import com.godeltech.springgodelbot.exception.RequestNotFoundException;
import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.service.OfferService;
import com.godeltech.springgodelbot.service.RequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.var;
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

    private final Map<Long, ChangeDriverRequest> changeDriverRequests;

    private final OfferService offerService;


    public DriverRequest getDriverRequest(Message message) {
        log.debug("Getting supplier request with chatId: {}", message.getChatId());
        if (driverRequests.containsKey(message.getChatId()))
            return driverRequests.get(message.getChatId());
        throw new RequestNotFoundException(DriverRequest.class, "chatId", message.getChatId(), message);
    }

    @Override
    public void savePassengerRequest(PassengerRequest passengerRequest) {
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
    public ChangeDriverRequest getChangeOfferRequest(Message message) {
        log.debug("Get change offer request by chat id: {}", message.getChatId());
        if (changeDriverRequests.containsKey(message.getChatId()))
            return changeDriverRequests.get(message.getChatId());
        throw new RequestNotFoundException(ChangeDriverRequest.class, "chatId", message.getChatId(), message);
    }

    @Override
    public void updateDates(ChangeDriverRequest changeDriverRequest) {
        log.debug("Update dates of offer with id: {}", changeDriverRequest.getOfferId());
        offerService.updateDatesOfOffer(changeDriverRequest);
        changeDriverRequests.remove(changeDriverRequest.getChatId());
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
    public void updateDescriptionOfOffer(ChangeDriverRequest changeDriverRequest) {
        log.debug("Update description of offer with offer id: {}", changeDriverRequest.getOfferId());
        offerService.updateDescriptionOfOffer(changeDriverRequest);
        changeDriverRequests.remove(changeDriverRequest.getChatId());
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
            driverRequests.remove(chatId);
            changeDriverRequests.remove(chatId);
    }

    @Override
    public void savePassenger(PassengerRequest passengerRequest) {
        var chatId = passengerRequest.getChatId();
        offerService.save(passengerRequest);
        passengerRequests.remove(chatId);
    }

    @Override
    public boolean existsPassengerRequestByChatId(Long chatId) {
        return passengerRequests.containsKey(chatId);
    }

    @Override
    public void updateRouteOfOffer(ChangeDriverRequest changeDriverRequest) {
        log.info("Update route of offer with id :{}", changeDriverRequest.getOfferId());
        var chatId = changeDriverRequest.getChatId();
        offerService.updateRoute(changeDriverRequest);
        changeDriverRequests.remove(chatId);
    }

    @Override
    public ChangeDriverRequest deleteOffer(Message message) {
        ChangeDriverRequest changeOfferRequest = getChangeOfferRequest(message);
        log.info("Delete offer with id : {}", changeOfferRequest.getOfferId());
        offerService.deleteById(changeOfferRequest.getOfferId(), message.getChatId());
        changeDriverRequests.remove(message.getChatId());
        return changeOfferRequest;

    }

    @Override
    public void checkAndClearChangingOfferRequests(Long chatId) {
        log.info("Check and clear if exists changeSupplierRequests by key:{}", chatId);
            changeDriverRequests.remove(chatId);
    }

    @Override
    public List<ChangeDriverRequest> findByUserIdAndActivity(Long id, Activity activity) {
        log.info("Find offers by id:{} and activity :{}", id, activity);
        return offerService.findByUserEntityIdAndActivity(id, activity);
    }

    @Override
    public ChangeDriverRequest addNewChangeOfferRequest(long offerId, Long chatId) {
        ChangeDriverRequest request = offerService.getById(offerId, chatId);
        request.setChatId(chatId);
        changeDriverRequests.put(chatId, request);
        return request;
    }

    @Override
    public List<PassengerRequest> findPassengersByRequestData(Request request) {
        log.info("Find passengers by secondDate:{},firstDate:{},routes:{}", request.getSecondDate(),
                request.getFirstDate(), request.getCities());
        return offerService.findPassengersByFirstDateBeforeAndSecondDateAfterAndRoutes(request.getSecondDate(),
                request.getFirstDate(), request.getCities());
    }

    @Override
    public List<DriverRequest> findDriversByRequestData(Request request) {
        log.info("Find drivers by secondDate:{},firstDate:{},routes:{}", request.getSecondDate(),
                request.getFirstDate(), request.getCities());
        return offerService.findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes(request.getSecondDate(),
                request.getFirstDate(), request.getCities());
    }


    public void saveDriverRequest(DriverRequest driverRequest) {
        log.debug("Save supplier request: {}", driverRequest);
        driverRequests.put(driverRequest.getChatId(), driverRequest);

    }
}
