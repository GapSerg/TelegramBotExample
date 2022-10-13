package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.mapper.OfferMapper;
import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.repository.OfferRepository;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;
    private final CityService cityService;

    @Override
    @Transactional
    public Offer save(DriverRequest driverRequest, User user, Message message) {
        log.info("Save supplier by: {}", driverRequest);
        List<City> cities = cityService.findCitiesByName(driverRequest.getCities(), message, user);
        Offer offer = offerMapper.mapToOffer(driverRequest, user, cities);
        return offerRepository.save(offer);
    }

    @Override
    public List<Offer> findPassengersByFirstDateBeforeAndSecondDateAfterAndCities
            (LocalDate secondDate, LocalDate firstDate, List<String> cities) {
        log.info("Find passengers by first date :{} and second date:{} with cities:{}", firstDate, secondDate, cities);

        return secondDate == null ?
                offerRepository.findByFirstDateAndCitiesAndActivity(firstDate, Activity.PASSENGER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList()) :
                offerRepository.findByDatesAndCitiesAndActivity(secondDate, firstDate, Activity.PASSENGER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList());
    }

    @Override
    public List<Offer> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<String> cities) {
        log.info("Find drivers by first date :{} and second date:{} with cities:{}", firstDate, secondDate, cities);
        return secondDate == null ?
                offerRepository.findByFirstDateAndCitiesAndActivity(firstDate, Activity.DRIVER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList()) :
                offerRepository.findByDatesAndCitiesAndActivity(secondDate, firstDate, Activity.DRIVER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList());
    }


    @Override
    public List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity, Message message, User user) {
        log.info("Find offers by id:{} and activity :{}", id, activity);
        var list = offerRepository.findByUserEntityIdAndActivity(id, activity);

        return list.stream()
                .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                .map(offerMapper::mapToChangeOfferRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeOfferRequest getById(Long offerId, Message message, User user) {
        log.info("Find offer by id : {}", offerId);
       return offerRepository.findById(offerId)
                .map(offer -> {
                    List<City> cities = cityService.findCitiesByOfferId(offerId);
                    offer.setCities(cities);
                    return offerMapper.mapToChangeOfferRequest(offer);
                })
                .orElseThrow(() -> new ResourceNotFoundException(Offer.class, "id", offerId, message, user));
    }

    @Override
    @Transactional
    public void deleteById(Long offerId, Message message, User user) {
        log.info("Delete offer by id : {}", offerId);
        Offer offer = getOfferById(offerId, message, user);
        offerRepository.delete(offer);
    }

    private Offer getOfferById(Long offerId, Message message, User user) {
        log.info("Get offer by id: {}", offerId);
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(Offer.class, "id", offerId, message, user));
        offer.setCities(cityService.findCitiesByOfferId(offerId));
        return offer;
    }

    @Override
    @Transactional
    public void updateCities(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update cities of offer with id : {} and cities : {} ", changeOfferRequest.getOfferId(),
                changeOfferRequest.getCities());
        Offer offer = getOfferById(changeOfferRequest.getOfferId(), message, user);
        List<City> cities = cityService.findCitiesByName(changeOfferRequest.getCities(), message, user);
        offer.setCities(cities);
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void updateDatesOfOffer(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update date of offer with first date : {} , and second date : {} ", changeOfferRequest.getFirstDate()
                , changeOfferRequest.getSecondDate());
        Offer offer = getOfferById(changeOfferRequest.getOfferId(), message, user);
        offer.setFirstDate(changeOfferRequest.getFirstDate());
        offer.setSecondDate(changeOfferRequest.getSecondDate() == null ?
                null :
                changeOfferRequest.getSecondDate());
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update description of offer with id : {}", changeOfferRequest.getOfferId());
        Offer offer = getOfferById(changeOfferRequest.getOfferId(), message, user);
        offer.setDescription(changeOfferRequest.getDescription());
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void deleteBySecondDateAfter(LocalDate date) {
        log.info("Delete offers whose second date is earlier than : {}", date);
        offerRepository.deleteOffersBySecondDateBeforeAndSecondDateIsNotNull(date);
    }

    @Override
    @Transactional
    public void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date) {
        log.info("Delete offers whose first date is earlier than :{} and second date is null", date);
        offerRepository.deleteOffersByFirstDateBeforeAndSecondDateIsNull(date);
    }

    @Override
    @Transactional
    public Offer save(PassengerRequest passengerRequest, User user, Message message) {
        log.info("Save passenger request : {}", passengerRequest);
        List<City> cities = cityService.findCitiesByName(passengerRequest.getCities(), message, user);
        Offer offer = offerMapper.mapToOffer(passengerRequest, user, cities);
        return offerRepository.save(offer);
    }

    private boolean checkRoute(List<String> cities, Offer offer) {
        boolean result = false;

        int difference = cities.size() < offer.getCities().size() ?
                cities.size() - offer.getCities().size() :
                offer.getCities().size() - cities.size();
        List<String> offerCities = offer.getCities().stream()
                .map(City::getName)
                .collect(Collectors.toList());
        int matches = 0;
        int previousSupplierIndex = -1;
        for (int i = 0; i < cities.size(); i++) {
            var route = cities.get(i);
            int supplierIndex = offerCities.lastIndexOf(route);
            if (supplierIndex != -1 &&
                    i >= difference &&
                    previousSupplierIndex <= supplierIndex) {
                matches++;
                previousSupplierIndex = supplierIndex;
            }
            if (matches == 2) {
                result = true;
                break;
            }
        }
        return result;
    }
}
