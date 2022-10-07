package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.dto.ChangeOfferRequest;
import com.godeltech.springgodelbot.dto.DriverRequest;
import com.godeltech.springgodelbot.dto.PassengerRequest;
import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.mapper.OfferMapper;
import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.entity.Offer;
import com.godeltech.springgodelbot.model.repository.OfferRepository;
import com.godeltech.springgodelbot.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class OfferServiceImpl implements OfferService {

    private final OfferRepository offerRepository;
    private final OfferMapper offerMapper;

    @Override
    @Transactional
    public Offer save(DriverRequest driverRequest) {
        log.info("Save supplier by: {}", driverRequest);
        Offer offer = offerMapper.mapToOffer(driverRequest);
        return offerRepository.save(offer);
    }

    @Override
    public List<PassengerRequest> findPassengersByFirstDateBeforeAndSecondDateAfterAndCities
            (LocalDate secondDate, LocalDate firstDate, List<City> cities) {
        log.info("Find passengers by first date :{} and second date:{} with cities:{}", firstDate, secondDate, cities);
        List<String> routeList = cities.stream()
                .map(City::getName)
                .collect(Collectors.toList());
        return secondDate == null ?
                offerRepository.findByFirstDateAndRoutesAndActivity(firstDate, Activity.PASSENGER.name(), routeList).stream()
                        .map(offerMapper::mapToPassengerRequest)
                        .collect(Collectors.toList()) :
                offerRepository.findByDatesAndRoutesAndActivity(secondDate, firstDate, Activity.PASSENGER.name(), routeList).stream()
                        .map(offerMapper::mapToPassengerRequest)
                        .collect(Collectors.toList());
    }

    @Override
    public List<DriverRequest> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<City> cities) {
        log.info("Find drivers by first date :{} and second date:{} with cities:{}", firstDate, secondDate, cities);
        List<String> routeList = cities.stream()
                .map(City::getName)
                .collect(Collectors.toList());
        return secondDate == null ?
                offerRepository.findByFirstDateAndRoutesAndActivity(firstDate, Activity.DRIVER.name(), routeList).stream()
                        .map(offerMapper::mapToDriverRequest)
                        .collect(Collectors.toList()) :
                offerRepository.findByDatesAndRoutesAndActivity(secondDate, firstDate, Activity.DRIVER.name(), routeList).stream()
                        .map(offerMapper::mapToDriverRequest)
                        .collect(Collectors.toList());
    }


    @Override
    public List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity) {
        log.info("Find offers by id:{} and activity :{}", id, activity);
        return offerRepository.findByUserEntityIdAndActivity(id, activity)
                .stream().map(offerMapper::mapToChangeOfferRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeOfferRequest getById(Long offerId, Long chatId) {
        log.info("Find offer by id : {}", offerId);
        return offerRepository.findById(offerId)
                .map(offerMapper::mapToChangeOfferRequest)
                .orElseThrow(() -> new ResourceNotFoundException(Offer.class, "id", offerId, chatId));
    }

    @Override
    @Transactional
    public void deleteById(Long offerId, Long chatId) {
        log.info("Delete offer by id : {}", offerId);
        Offer offer = getOfferById(offerId, chatId);
        offerRepository.delete(offer);
    }

    private Offer getOfferById(Long offerId, Long chatId) {
        log.info("Get offer by id: {}", offerId);
        return offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(Offer.class, "id", offerId, chatId));

    }

    @Override
    @Transactional
    public void updateCities(ChangeOfferRequest changeOfferRequest) {
        log.info("Update cities of offer with id : {} and cities : {} ", changeOfferRequest.getOfferId(),
                changeOfferRequest.getCities());
        Offer offer = getOfferById(changeOfferRequest.getOfferId(), changeOfferRequest.getChatId());
        offer.setCities(changeOfferRequest.getCities());
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void updateDatesOfOffer(ChangeOfferRequest changeOfferRequest) {
        log.info("Update date of offer with first date : {} , and second date : {} ", changeOfferRequest.getFirstDate()
                , changeOfferRequest.getSecondDate());
        Offer offer = getOfferById(changeOfferRequest.getOfferId(), changeOfferRequest.getChatId());
        offer.setFirstDate(changeOfferRequest.getFirstDate());
        offer.setSecondDate(changeOfferRequest.getSecondDate());
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void updateDescriptionOfOffer(ChangeOfferRequest changeOfferRequest) {
        log.info("Update description of offer with id : {}", changeOfferRequest.getOfferId());
        Offer offer = getOfferById(changeOfferRequest.getOfferId(), changeOfferRequest.getChatId());
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
        log.info("Delete offers whose first date is earlier than :{} and second date is null",date);
        offerRepository.deleteOffersByFirstDateAfterAndSecondDateIsNull(date);
    }

    @Override
    @Transactional
    public Offer save(PassengerRequest passengerRequest) {
        log.info("Save passenger request : {}", passengerRequest);
        Offer offer = offerMapper.mapToOffer(passengerRequest);
        return offerRepository.save(offer);
    }
}
