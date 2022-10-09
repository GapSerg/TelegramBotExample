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

import java.util.List;

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

    @Override
    @Cacheable(value = "routes", key = "#routeId")
    public City getById(Integer routeId, Long chatId) {
        log.info("Get route by id:{}", routeId);
        return cityRepository.findById(routeId)
                .orElseThrow(() -> new ResourceNotFoundException(City.class, "routeId", routeId, chatId));
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
            throw new ResourceNotUniqueException(City.class,"name",city.getName());
        return cityRepository.save(city);
    }

}
