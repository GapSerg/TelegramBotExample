package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.City;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface CityService {
    List<City> findAll();

//    City getById(Integer routeId, Long chatId);

    void deleteById(Integer routeId);

    City save(City city);

    List<City> findCitiesByName(List<String> cities, Message message, User user);

    List<City> findCitiesByOfferId(Long id);
}
