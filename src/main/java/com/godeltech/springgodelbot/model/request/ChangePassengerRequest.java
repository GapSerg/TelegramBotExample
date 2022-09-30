package com.godeltech.springgodelbot.model.request;

import com.godeltech.springgodelbot.model.entity.City;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChangePassengerRequest {
    private Long passengerId;
    private Long chatId;
    private List<City> cities;
    private LocalDate firstDate;
    private LocalDate secondDate;
    private String description;
    private Long userId;
    private List<Integer> messages;
    private Boolean isNeedForDescription;
}
