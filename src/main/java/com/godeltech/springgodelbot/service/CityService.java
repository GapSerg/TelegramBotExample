package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.City;

import java.util.List;

public interface CityService {
    List<City> findAll();

//    City getById(Integer routeId, Long chatId);

    void deleteById(Integer routeId);

    City save(City city);
}
