package com.godeltech.springgodelbot.scheduler;

public interface ScheduleService {
    void deleteExpireOffers();
    void deleteExpireTokens();
}
