package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.mapper.TripOfferMapper;
import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.repository.TripOfferRepository;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.TripOfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class TripOfferServiceImpl implements TripOfferService {

    private final TripOfferRepository tripOfferRepository;
    private final TripOfferMapper tripOfferMapper;
    private final CityService cityService;

    @Override
    @Transactional
    public TripOffer save(DriverRequest driverRequest, User user, Message message) {
        log.info("Save supplier by: {}", driverRequest);
        List<City> cities = cityService.findCitiesByName(driverRequest.getCities(), message, user);
        TripOffer offer = tripOfferMapper.mapToOffer(driverRequest, user, cities);
        return tripOfferRepository.save(offer);
    }


    @Override
    public List<TripOffer> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<String> cities) {
        log.info("Find drivers by first date :{} and second date:{} with cities:{}", firstDate, secondDate, cities);
        return secondDate == null ?
                tripOfferRepository.findByFirstDateAndCitiesAndActivity(firstDate, Activity.DRIVER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList()) :
                tripOfferRepository.findByDatesAndCitiesAndActivity(secondDate, firstDate, Activity.DRIVER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList());
    }


    @Override
    public List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity, Message message, User user) {
        log.info("Find offers by id:{} and activity :{}", id, activity);
        var list = tripOfferRepository.findByUserEntityId(id);

        return list.stream()
                .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                .map(tripOfferMapper::mapToChangeOfferRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeOfferRequest getById(Long offerId, Message message, User user) {
        log.info("Find offer by id : {}", offerId);
       return tripOfferRepository.findById(offerId)
                .map(offer -> {
                    List<City> cities = cityService.findCitiesByOfferId(offerId);
                    offer.setCities(cities);
                    return tripOfferMapper.mapToChangeOfferRequest(offer);
                })
                .orElseThrow(() -> new ResourceNotFoundException(TripOffer.class, "id", offerId, message, user));
    }

    @Override
    @Transactional
    public void deleteById(Long offerId, Message message, User user) {
        log.info("Delete driver by id : {}", offerId);
        TripOffer tripOffer = getTripOfferById(offerId, message, user);
        tripOfferRepository.delete(tripOffer);
    }

    private TripOffer getTripOfferById(Long offerId, Message message, User user) {
        log.info("Get offer by id: {}", offerId);
        TripOffer tripOffer = tripOfferRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(TripOffer.class, "id", offerId, message, user));
        tripOffer.setCities(cityService.findCitiesByOfferId(offerId));
        return tripOffer;
    }

    @Override
    @Transactional
    public void updateCities(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update cities of tripOffer with id : {} and cities : {} ", changeOfferRequest.getOfferId(),
                changeOfferRequest.getCities());
        TripOffer tripOffer = getTripOfferById(changeOfferRequest.getOfferId(), message, user);
        List<City> cities = cityService.findCitiesByName(changeOfferRequest.getCities(), message, user);
        tripOffer.setCities(cities);
        tripOfferRepository.save(tripOffer);
    }

    @Override
    @Transactional
    public void updateDatesOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update date of driver with first date : {} , and second date : {} ", changeOfferRequest.getFirstDate()
                , changeOfferRequest.getSecondDate());
        TripOffer tripOffer= getTripOfferById(changeOfferRequest.getOfferId(), message, user);
        tripOffer.setFirstDate(changeOfferRequest.getFirstDate());
        tripOffer.setSecondDate(changeOfferRequest.getSecondDate() == null ?
                null :
                changeOfferRequest.getSecondDate());
        tripOfferRepository.save(tripOffer);
    }

    @Override
    @Transactional
    public void updateDescriptionOfTripOffer(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update description of driver with id : {}", changeOfferRequest.getOfferId());
        TripOffer tripOffer = getTripOfferById(changeOfferRequest.getOfferId(), message, user);
        tripOffer.setDescription(changeOfferRequest.getDescription());
        tripOfferRepository.save(tripOffer);
    }

    @Override
    @Transactional
    public void deleteBySecondDateAfter(LocalDate date) {
        log.info("Delete drivers whose second date is earlier than : {}", date);
        tripOfferRepository.deleteOffersBySecondDateBeforeAndSecondDateIsNotNull(date);
    }

    @Override
    @Transactional
    public void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date) {
        log.info("Delete offers whose first date is earlier than :{} and second date is null", date);
        tripOfferRepository.deleteOffersByFirstDateBeforeAndSecondDateIsNull(date);
    }


    private boolean checkRoute(List<String> cities, TripOffer offer) {
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
