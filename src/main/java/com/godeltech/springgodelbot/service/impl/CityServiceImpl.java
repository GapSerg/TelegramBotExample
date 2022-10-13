package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.exception.ResourceNotUniqueException;
import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.model.repository.CityRepository;
import com.godeltech.springgodelbot.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class CityServiceImpl implements CityService {
    private final CityRepository cityRepository;


    @Override
    @Cacheable(value = "routes")
    public List<City> findAll() {
        log.info("Find all routes");
        return cityRepository.findAll();
    }

    @Cacheable(value = "routes", key = "#name")
    public City getByName(String name, Message message, User user) {
        return cityRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException(City.class, "name", name, message, user));
    }

    @Override
    @Transactional
    public void deleteById(Integer routeId) {
        log.info("Delete route by id:{}", routeId);
        if (!cityRepository.existsById(routeId)) {
            cityRepository.deleteById(routeId);
        }
    }

    @Override
    @Transactional
    public City save(City city) {
        log.info("Save route : {}", city);
        if (cityRepository.existsByName(city.getName()))
            throw new ResourceNotUniqueException(City.class, "name", city.getName());
        return cityRepository.save(city);
    }

    @Override
    public List<City> findCitiesByName(List<String> cities, Message message, User user) {
        return cities.stream()
                .map(city -> getByName(city, message, user))
                .collect(Collectors.toList());
    }

    @Override
    public List<City> findCitiesByOfferId(Long id) {
        log.info("Get ordered cities");
        return cityRepository.findCitiesByOfferId(id);
    }

}
