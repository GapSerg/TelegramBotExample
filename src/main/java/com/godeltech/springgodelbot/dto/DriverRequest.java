package com.godeltech.springgodelbot.dto;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.City;
import lombok.Builder;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public class DriverRequest extends Request {
    public DriverRequest() {
        super(Activity.DRIVER);
    }

    @Builder
    public DriverRequest(Long chatId, UserDto userDto, List<City> cities,
                         LocalDate firstDate, LocalDate secondDate,
                         String description, Boolean needForDescription,
                         Set<Integer> messages) {
        super(chatId, userDto, cities, firstDate, secondDate, description, needForDescription, messages, Activity.DRIVER);
    }
}
