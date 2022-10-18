package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.model.entity.ActivityType;
import com.godeltech.springgodelbot.model.repository.ActivityTypeRepository;
import com.godeltech.springgodelbot.service.ActivityTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityTypeServiceImpl implements ActivityTypeService {
    private final ActivityTypeRepository activityTypeRepository;
    @Override
    public List<ActivityType> getActivities(List<Activity> activities, Message message, User user) {
        log.info("Find activityTypes by : {} ",activities);
        return activities.stream()
                .map(activity -> getActivityType(activity,message ,user ))
                .collect(Collectors.toList());
    }

    @Override
    public ActivityType getActivityType(Activity activity, Message message, User user) {
        return activityTypeRepository.findByName(activity)
                .orElseThrow(()->new ResourceNotFoundException(ActivityType.class,"name",activity,message,user));
    }
}
