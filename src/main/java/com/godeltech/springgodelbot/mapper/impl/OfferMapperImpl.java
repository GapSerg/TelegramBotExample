package com.godeltech.springgodelbot.mapper.impl;

import com.godeltech.springgodelbot.mapper.OfferMapper;
import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.model.entity.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OfferMapperImpl implements OfferMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;


    @Override
    public Offer mapToOffer(Request request, User user, List<City> cities) {
        Offer offer = modelMapper.map(request, Offer.class);
        offer.setUserEntity(userMapper.mapToUserEntity(user));
        offer.setCities(cities);
        return offer;
    }

    @Override
    public DriverRequest mapToDriverRequest(Offer offer) {
        DriverRequest request = modelMapper.map(offer, DriverRequest.class);
//        request.setUserDto(offer);
        return request;
    }

    @Override
    public PassengerRequest mapToPassengerRequest(Offer offer) {
        PassengerRequest request = modelMapper.map(offer, PassengerRequest.class);
//        request.setUserDto(userMapper.mapToUserDto(offer.getUserEntity()));
        return request;
    }

    @Override
    public ChangeOfferRequest mapToChangeOfferRequest(Offer offer) {
        ChangeOfferRequest changeOfferRequest = ChangeOfferRequest.builder()
                .offerId(offer.getId())
                .activity(offer.getActivity())
                .description(offer.getDescription())
                .firstDate(offer.getFirstDate())
                .secondDate(offer.getSecondDate())
                .cities(offer.getCities().stream()
                        .map(City::getName)
                        .collect(Collectors.toList()))
                .build();
        return changeOfferRequest;
    }
}