package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.model.entity.ActivityType;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

public interface ActivityTypeService {
    List<ActivityType> getActivities(List<Activity> activities, Message message, User user);

    ActivityType getActivityType(Activity activity, Message message, User user);
}
