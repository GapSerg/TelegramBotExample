package com.godeltech.springgodelbot.mapper.impl;

import com.godeltech.springgodelbot.dto.*;
import com.godeltech.springgodelbot.mapper.OfferMapper;
import com.godeltech.springgodelbot.mapper.UserMapper;
import com.godeltech.springgodelbot.model.entity.Offer;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OfferMapperImpl implements OfferMapper {

    private final ModelMapper modelMapper;
    private final UserMapper userMapper;


    @Override
    public Offer mapToOffer(Request request) {
        Offer offer = modelMapper.map(request, Offer.class);
        offer.setUserEntity(userMapper.mapToUserEntity(request.getUserDto()));
        return offer;
    }

    @Override
    public DriverRequest mapToDriverRequest(Offer offer) {
        DriverRequest request = modelMapper.map(offer, DriverRequest.class);
        request.setUserDto(userMapper.mapToUserDto(offer.getUserEntity()));
        return request;
    }

    @Override
    public PassengerRequest mapToPassengerRequest(Offer offer) {
        PassengerRequest request = modelMapper.map(offer, PassengerRequest.class);
        request.setUserDto(userMapper.mapToUserDto(offer.getUserEntity()));
        return request;
    }

    @Override
    public ChangeDriverRequest mapToChangeOfferRequest(Offer offer) {
        UserDto userDto = userMapper.mapToUserDto(offer.getUserEntity());
        ChangeDriverRequest changeDriverRequest = ChangeDriverRequest.builder()
                .offerId(offer.getId())
                .activity(offer.getActivity())
                .description(offer.getDescription())
                .firstDate(offer.getFirstDate())
                .secondDate(offer.getSecondDate())
                .cities(offer.getCities())
                .build();
        changeDriverRequest.setUserDto(userDto);
        return changeDriverRequest;
    }
}