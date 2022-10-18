package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.exception.UnknownCommandException;
import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.model.repository.RequestRepository;
import com.godeltech.springgodelbot.service.DriverItemService;
import com.godeltech.springgodelbot.service.RequestService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.TransferItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {

    //
//    private final OfferService offerService;
    private final TransferItemService transferItemService;
    private final DriverItemService driverItemService;
    private final TokenService tokenService;

    private final RequestRepository requestRepository;


    @Override
    @Transactional
    public void saveDriver(Request request, Message message, User user) {
        log.debug("Saving driver request with token: {}", message);
        driverItemService.save((DriverRequest) request, user, message);
        request.setNeedForDescription(false);
        updateRequest(request, message, user);
    }

    @Override
    @Transactional
    public void updateDates(ChangeOfferRequest changeOfferRequest, String token, Message message, User user) {
        log.debug("Update dates of offer with id: {} and token: {}", changeOfferRequest.getOfferId(), token);
        if (changeOfferRequest.getActivity() == Activity.DRIVER) {
            driverItemService.updateDatesOfTripOffer(changeOfferRequest, message, user);
        } else {
            transferItemService.updateDatesOfTransferItem(changeOfferRequest, message, user);
        }
    }


    @Override
    @Transactional
    public void updateDescriptionOfOffer(Request request, Message message, User user) {
        log.debug("Update description of offer with offer id: {} and token: {}", request.getOfferId(), request.getToken().getId());
        if (request.getActivity() == Activity.DRIVER) {
            driverItemService.updateDescriptionOfTripOffer((ChangeOfferRequest) request, message, user);
        } else {
            transferItemService.updateDescriptionOfTransferItem((ChangeOfferRequest) request, message, user);
        }
        request.setNeedForDescription(false);
        updateRequest(request, message, user);
    }

    @Transactional
    @Override
    public void savePassenger(Request request, Message message, User user) {
        log.debug("Save passenger request : {} and token: {}", request, message);
        transferItemService.save((PassengerRequest) request, user, message);
        request.setNeedForDescription(false);
        updateRequest(request, message, user);
    }

    @Override
    @Transactional
    public void updateRouteOfOffer(Request changeOfferRequest, String token, Message message, User user) {
        log.info("Update route of offer with id :{}", changeOfferRequest.getOfferId());
        if (changeOfferRequest.getActivity() == Activity.DRIVER) {
            driverItemService.updateCitiesOfDriverItem((ChangeOfferRequest) changeOfferRequest, message, user);
        } else {
            transferItemService.updateCitiesOfTransferItem((ChangeOfferRequest) changeOfferRequest, message, user);
        }
    }

    @Override
    public void deleteOffer(Message message, String token, User user) {
        Request request = getRequest(message, token, user);
        log.debug("Delete offer with id : {} and token: {}", request.getOfferId(), token);
        if (request.getActivity() == Activity.DRIVER) {
            driverItemService.deleteById(request.getOfferId(), message, user);
        } else {
            transferItemService.deleteById(request.getOfferId(), message, user);
        }
        deleteRequest(request, message);

    }

    @Override
    public List<ChangeOfferRequest> findUsersOffersByActivity(Long id, Activity activity, Message message, User user) {
        log.debug("Find offers by id:{} and activity :{}", id, activity);
        if (activity == Activity.DRIVER) {
            return driverItemService.findByUserEntityId(id, message, user);
        } else {
            return transferItemService.findByUserEntityIdAndActivity(id, activity, message, user);
        }
    }


    @Override
    public List<TransferItem> findPassengersByRequestData(Request request) {
        log.debug("Find passengers by secondDate:{},firstDate:{},routes:{}", request.getSecondDate(),
                request.getFirstDate(), request.getCities());
        return request.getSuitableActivities().size() == 2 ?
                transferItemService.findTransferItemsByFirstDateBeforeAndSecondDateAfterAndCities(request.getSecondDate(),
                        request.getFirstDate(), request.getCities()) :
                transferItemService.findTransferItemsByFirstDateBeforeAndSecondDateAfterAndCitiesAndActivity(request.getSecondDate(),
                        request.getFirstDate(), request.getCities(), request.getSuitableActivities().get(0));
    }

    @Override
    public List<DriverItem> findDriversByRequestData(Request request) {
        log.debug("Find drivers by secondDate:{},firstDate:{},routes:{}", request.getSecondDate(),
                request.getFirstDate(), request.getCities());
        return driverItemService.findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes(request.getSecondDate(),
                request.getFirstDate(), request.getCities());
    }

    @Override
    public Request findRequestByUserIdForSave(Message message) {
        log.info("Find request for saving with user id : {}", message.getFrom().getId());
        List<Request> requests = requestRepository.findByTokenUserIdAndNeedForDescriptionTrue(message.getFrom().getId());
        if (requests.isEmpty()) {
            return null;
        } else if (requests.size() == 1) {
            return requests.get(0);
        } else {
            throw new UnknownCommandException();
        }
    }

    @Override
    @Transactional
    public Request saveRequest(Request request, String tokenId, Message message, User user) {
        log.info("Save new request with tokenId: {} with activity : {}", tokenId, request.getActivity());
        Token token = tokenService.checkIncomeToken(tokenId, message, user);
        request.setToken(token);
        return requestRepository.save(request);
    }

    @Override
    public Request getRequest(Message message, String tokenId, User user) {
        log.info("Get request by token id : {}", tokenId);
        return requestRepository.findByTokenId(tokenId)
                .orElseThrow(() -> new ResourceNotFoundException(Request.class, "tokenId", tokenId, message, user));
    }

    @Override
    @Transactional
    public Request updateRequest(Request request, Message message, User user) {
        log.info("Update request with id : {}", request.getId());
        getById(request.getId(), message, user);
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public void deleteRequest(Request request, Message message) {
        log.info("Delete request with id : {}", request.getId());
//        Request necessaryRequest = getById(request.getId(), message);
        requestRepository.deleteById(request.getId());
    }

    @Override
    @Transactional
    public Request prepareRequestForDescription(Request request) {
        log.info("Prepare request with id :{} and user id : {} for save with description"
                , request.getId(), request.getToken().getUserId());
        requestRepository.setNeedForDescriptionInRequestsWithUserId(false, request.getToken().getUserId());
        request.setNeedForDescription(true);
        return requestRepository.save(request);
    }

    @Override
    @Transactional
    public ChangeOfferRequest refreshChangeOfferRequest(Request request, Message message, User user) {
        log.info("Refresh change offer request with id : {}", request.getOfferId());
        ChangeOfferRequest changeOffer;
        if (request.getActivity() == Activity.DRIVER) {
            changeOffer = driverItemService.getById(request.getOfferId(), message, user);
        } else {
            changeOffer = transferItemService.getById(request.getOfferId(), message, user);
        }
        changeOffer.setId(request.getId());
        changeOffer.setToken(request.getToken());
        return requestRepository.save(changeOffer);
    }

    @Override
    @Transactional
    public Request setOfferToRequest(long offerId, Request request, Message message, User user) {
        log.debug("Set offer  with id :{} to change offer request with id : {}  and token: {}",
                offerId, request.getId(), request.getToken().getId());
        ChangeOfferRequest changeOffer;
        if (request.getActivity() == Activity.DRIVER) {
            changeOffer = driverItemService.getById(offerId, message, user);
        } else {
            changeOffer = transferItemService.getById(offerId, message, user);
        }
        changeOffer.setToken(request.getToken());
        changeOffer.setId(request.getId());
        return requestRepository.save(changeOffer);
    }

    @Override
    @Transactional
    public Request getOrSaveRequest(Request request, String tokenId, Message message, User user) {
        log.info("Check request by tokenId :{}", tokenId);
        Optional<Request> possibleRequest = requestRepository.findByTokenId(tokenId);
        if (possibleRequest.isPresent()) {
            Request oldRequest = possibleRequest.get();
            request.setToken(oldRequest.getToken());
            request.setId(oldRequest.getId());
        } else {
            Token token = tokenService.getById(tokenId, message, user);
            request.setToken(token);
        }
        return requestRepository.save(request);

    }

    private Request getById(Long id, Message message, User user) {
        log.info("Get request by id : {}", id);
        return requestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Request.class, "id", id, message, user));
    }

}
