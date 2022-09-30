package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.dto.ChangeDriverRequest;
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
    public List<PassengerRequest> findPassengersByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<City> cities) {
        List<String> routeList = cities.stream()
                .map(City::getName)
                .collect(Collectors.toList());
        return offerRepository.findByDatesAndRoutesAndActivity(secondDate, firstDate, Activity.PASSENGER.name(), routeList).stream()
                .map(offerMapper::mapToPassengerRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<DriverRequest> findDriversByFirstDateBeforeAndSecondDateAfterAndRoutes
            (LocalDate secondDate, LocalDate firstDate, List<City> cities) {
        List<String> routeList = cities.stream()
                .map(City::getName)
                .collect(Collectors.toList());
        return offerRepository.findByDatesAndRoutesAndActivity(secondDate, firstDate, Activity.DRIVER.name(), routeList).stream()
                .map(offerMapper::mapToDriverRequest)
                .collect(Collectors.toList());
    }


    @Override
    public List<ChangeDriverRequest> findByUserEntityIdAndActivity(Long id, Activity activity) {
        log.info("Find offers by id:{} and activity :{}", id, activity);
        return offerRepository.findByUserEntityIdAndActivity(id, activity)
                .stream().map(offerMapper::mapToChangeOfferRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeDriverRequest getById(Long offerId, Long chatId) {
        return offerRepository.findById(offerId)
                .map(offerMapper::mapToChangeOfferRequest)
                .orElseThrow(() -> new ResourceNotFoundException(Offer.class, "id", offerId, chatId));
    }

    @Override
    @Transactional
    public void deleteById(Long offerId, Long chatId) {
        Offer offer = getOfferById(offerId, chatId);
        offerRepository.delete(offer);
    }

    private Offer getOfferById(Long offerId, Long chatId) {
        Offer offer = offerRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(Offer.class, "id", offerId, chatId));
        return offer;
    }

    @Override
    @Transactional
    public void updateRoute(ChangeDriverRequest changeDriverRequest) {
        Offer offer = getOfferById(changeDriverRequest.getOfferId(), changeDriverRequest.getChatId());
        offer.setCities(changeDriverRequest.getCities());
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void updateDatesOfOffer(ChangeDriverRequest changeDriverRequest) {
        Offer offer = getOfferById(changeDriverRequest.getOfferId(), changeDriverRequest.getChatId());
        offer.setFirstDate(changeDriverRequest.getFirstDate());
        offer.setSecondDate(changeDriverRequest.getSecondDate());
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void updateDescriptionOfOffer(ChangeDriverRequest changeDriverRequest) {
        Offer offer = getOfferById(changeDriverRequest.getOfferId(), changeDriverRequest.getChatId());
        offer.setDescription(changeDriverRequest.getDescription());
        offerRepository.save(offer);
    }

    @Override
    @Transactional
    public void deleteBySecondDateAfter(LocalDate date) {
        log.info("Delete suppliers whose second date is earlier than : {}", date);
        offerRepository.deleteDriversBySecondDateBefore(date);
    }

    @Override
    @Transactional
    public Offer save(PassengerRequest passengerRequest) {
        Offer offer = offerMapper.mapToOffer(passengerRequest);
        return offerRepository.save(offer);
    }

    private boolean checkRoute(List<City> cities, Offer offer) {
        boolean result = false;
        int difference = cities.size() < offer.getCities().size() ?
                cities.size() - offer.getCities().size() :
                offer.getCities().size() - cities.size();
        int matches = 0;
        int previousSupplierIndex = -1;
        for (int i = 0; i < cities.size(); i++) {
            var route = cities.get(i);
            int supplierIndex = offer.getCities().lastIndexOf(route);
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
