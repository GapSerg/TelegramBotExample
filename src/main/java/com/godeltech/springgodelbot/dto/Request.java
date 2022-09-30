package com.godeltech.springgodelbot.dto;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Data
public abstract class Request {
    private Long chatId;
    private UserDto userDto;
    private List<City> cities;
    private LocalDate firstDate;
    private LocalDate secondDate;
    private String description;
    private Boolean needForDescription;
    private Set<Integer> messages;
    private final Activity activity;

    public Request(Activity activity) {
        this.activity=activity;
    }

    public Request(Long chatId, UserDto userDto, List<City> cities, LocalDate firstDate, LocalDate secondDate, String description, Boolean needForDescription, Set<Integer> messages, Activity activity) {
        this.chatId = chatId;
        this.userDto = userDto;
        this.cities = cities;
        this.firstDate = firstDate;
        this.secondDate = secondDate;
        this.description = description;
        this.needForDescription = needForDescription;
        this.messages = messages;
        this.activity = activity;
    }
}
