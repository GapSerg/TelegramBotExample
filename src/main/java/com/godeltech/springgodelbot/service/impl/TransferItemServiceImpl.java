package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.mapper.TransferItemMapper;
import com.godeltech.springgodelbot.model.entity.*;
import com.godeltech.springgodelbot.model.repository.TransferItemRepository;
import com.godeltech.springgodelbot.service.CityService;
import com.godeltech.springgodelbot.service.TransferItemService;
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
public class TransferItemServiceImpl implements TransferItemService {

    private final TransferItemRepository transferItemRepository;
    private final TransferItemMapper transferItemMapper;
    private final CityService cityService;


    @Override
    public List<TransferItem> findPassengersByFirstDateBeforeAndSecondDateAfterAndCities
            (LocalDate secondDate, LocalDate firstDate, List<String> cities) {
        log.info("Find transfer items by first date :{} and second date:{} with cities:{}", firstDate, secondDate, cities);

        return secondDate == null ?
                transferItemRepository.findByFirstDateAndCitiesAndActivity(firstDate, Activity.PASSENGER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList()) :
                transferItemRepository.findByDatesAndCitiesAndActivity(secondDate, firstDate, Activity.PASSENGER.name(), cities).stream()
                        .peek(offer -> offer.setCities(cityService.findCitiesByOfferId(offer.getId())))
                        .filter(offer -> checkRoute(cities, offer))
                        .collect(Collectors.toList());
    }


    @Override
    public List<ChangeOfferRequest> findByUserEntityIdAndActivity(Long id, Activity activity, Message message, User user) {
        log.info("Find transfer item by id:{} and activity :{}", id, activity);
        return transferItemRepository.findByUserEntityIdAndActivity(id, activity).stream()
                .peek(item -> item.setCities(cityService.findCitiesByOfferId(item.getId())))
                .map(transferItemMapper::mapToChangeOfferRequest)
                .collect(Collectors.toList());
    }

    @Override
    public ChangeOfferRequest getById(Long itemId, Message message, User user) {
        log.info("Find transfer item by id : {}", itemId);
        return transferItemRepository.findById(itemId)
                .map(item -> {
                    List<City> cities = cityService.findCitiesByOfferId(itemId);
                    item.setCities(cities);
                    return transferItemMapper.mapToChangeOfferRequest(item);
                })
                .orElseThrow(() -> new ResourceNotFoundException(Offer.class, "id", itemId, message, user));
    }

    @Override
    @Transactional
    public void deleteById(Long offerId, Message message, User user) {
        log.info("Delete transfer item by id : {}", offerId);
        TransferItem item = getOfferById(offerId, message, user);
        transferItemRepository.delete(item);
    }

    private TransferItem getOfferById(Long offerId, Message message, User user) {
        log.info("Get transfer item by id: {}", offerId);
        TransferItem item = transferItemRepository.findById(offerId)
                .orElseThrow(() -> new ResourceNotFoundException(TransferItem.class, "id", offerId, message, user));
        item.setCities(cityService.findCitiesByOfferId(offerId));
        return item;
    }

    @Override
    @Transactional
    public void updateCitiesOfTransferItem(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update cities of transfer item with id : {} and cities : {} ", changeOfferRequest.getOfferId(),
                changeOfferRequest.getCities());
        TransferItem item = getOfferById(changeOfferRequest.getOfferId(), message, user);
        List<City> cities = cityService.findCitiesByName(changeOfferRequest.getCities(), message, user);
        item.setCities(cities);
        transferItemRepository.save(item);
    }

    @Override
    @Transactional
    public void updateDatesOfTransferItem(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update date of transfer item with first date : {} , and second date : {} ", changeOfferRequest.getFirstDate()
                , changeOfferRequest.getSecondDate());
        TransferItem item = getOfferById(changeOfferRequest.getOfferId(), message, user);
        item.setFirstDate(changeOfferRequest.getFirstDate());
        item.setSecondDate(changeOfferRequest.getSecondDate() == null ?
                null :
                changeOfferRequest.getSecondDate());
        transferItemRepository.save(item);
    }

    @Override
    @Transactional
    public void updateDescriptionOfTransferItem(ChangeOfferRequest changeOfferRequest, Message message, User user) {
        log.info("Update description of transfer item with id : {}", changeOfferRequest.getOfferId());
        TransferItem item = getOfferById(changeOfferRequest.getOfferId(), message, user);
        item.setDescription(changeOfferRequest.getDescription());
        transferItemRepository.save(item);
    }

    @Override
    @Transactional
    public void deleteBySecondDateAfter(LocalDate date) {
        log.info("Delete offers whose second date is earlier than : {}", date);
        transferItemRepository.deleteOffersBySecondDateBeforeAndSecondDateIsNotNull(date);
    }

    @Override
    @Transactional
    public void deleteByFirstDateAfterWhereSecondDateIsNull(LocalDate date) {
        log.info("Delete offers whose first date is earlier than :{} and second date is null", date);
        transferItemRepository.deleteOffersByFirstDateBeforeAndSecondDateIsNull(date);
    }

    @Override
    @Transactional
    public TransferItem save(PassengerRequest passengerRequest, User user, Message message) {
        log.info("Save passenger request : {}", passengerRequest);
        List<City> cities = cityService.findCitiesByName(passengerRequest.getCities(), message, user);
        TransferItem transferItem = transferItemMapper.mapToTransferItem(passengerRequest, user, cities);
        return transferItemRepository.save(transferItem);
    }

    private boolean checkRoute(List<String> cities, TransferItem transferItem) {
        boolean result = false;

        int difference = cities.size() < transferItem.getCities().size() ?
                cities.size() - transferItem.getCities().size() :
                transferItem.getCities().size() - cities.size();
        List<String> offerCities = transferItem.getCities().stream()
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
