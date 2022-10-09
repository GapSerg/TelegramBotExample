package com.godeltech.springgodelbot.controller;

import com.godeltech.springgodelbot.model.entity.City;
import com.godeltech.springgodelbot.service.CityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cities")
@RequiredArgsConstructor
@Slf4j
public class CityController {

    private final CityService cityService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<City> getAllCities(){
        log.info("Get all cities ");
        return cityService.findAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public City saveCity(@RequestBody City city){
        log.info("Save city : {}",city);
        return cityService.save(city);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteCityById(@PathVariable Integer id){
        log.info("Delete city by id : {}",id);
        cityService.deleteById(id);
    }
}
