//package com.godeltech.springgodelbot.mapper.impl;
//
//import com.godeltech.springgodelbot.mapper.PassengerMapper;
//import com.godeltech.springgodelbot.model.entity.UserEntity;
//import com.godeltech.springgodelbot.dto.PassengerRequest;
//import lombok.RequiredArgsConstructor;
//import org.modelmapper.ModelMapper;
//import org.springframework.stereotype.Component;
//
//@Component
//@RequiredArgsConstructor
//public class PassengerMapperImpl implements PassengerMapper {
//
//    private final ModelMapper modelMapper;
//
//    @Override
//    public Passenger mapToPassenger(PassengerRequest passengerRequest, UserEntity user) {
//        var passenger = modelMapper.map(passengerRequest, Passenger.class);
//        passenger.setUserEntity(user);
//        return passenger;
//    }
//
//    @Override
//    public PassengerRequest mapToPassengerRequest(Passenger passenger) {
//        return modelMapper.map(passenger, PassengerRequest.class);
//    }
//}
