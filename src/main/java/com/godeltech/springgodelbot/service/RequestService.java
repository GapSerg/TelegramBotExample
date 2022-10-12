package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.ChangeOfferRequest;
import com.godeltech.springgodelbot.model.entity.Offer;
import com.godeltech.springgodelbot.model.entity.Request;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.Token;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.List;

public interface RequestService {

    void saveDriver(Request driverRequest, Message message, User user);


    void updateDates(ChangeOfferRequest changeOfferRequest, String token, Message message, User user);


    void updateDescriptionOfOffer(Request request, Message message);



    void savePassenger(Request request, Message message, User user);


    void updateRouteOfOffer(Request changeOfferRequest, String token, Message message, User user);

    void deleteOffer(Message message, String token, User user);

    List<ChangeOfferRequest> findUsersOffersByActivity(Long id, Activity activity);


    List<Offer> findPassengersByRequestData(Request request);

    List<Offer> findDriversByRequestData(Request request);

    Request findRequestByUserIdForSave(Message message);

    Request saveRequest(Request request, String tokenId, Message message, User user);

    Request getRequest(Message message, String tokenId, User user);

    Request updateRequest(Request request, Message message, User user);

    void deleteRequest(Request request, Message message);

    Request prepareRequestForDescription(Request request);

    ChangeOfferRequest refreshChangeOfferRequest(Request changeOfferRequest, Message message, User user);

    Request setOfferToRequest(long offerId, Request request, Message message, User user);

    Request getOrSaveRequest(Request request, String token, Message message, User user);

    void deleteAllByTokens(List<Token> tokens);

    void deleteNonUsableExpiredTokens(LocalDateTime date);
}
