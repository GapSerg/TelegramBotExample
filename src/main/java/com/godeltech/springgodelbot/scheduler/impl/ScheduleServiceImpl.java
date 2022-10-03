package com.godeltech.springgodelbot.scheduler.impl;

import com.godeltech.springgodelbot.scheduler.ScheduleService;
import com.godeltech.springgodelbot.service.OfferService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final OfferService offerService;


    @Override
    @Scheduled(initialDelayString = "${schedule.start}", fixedDelayString = "${schedule.work}")
    public void deleteExpireOffers() {
        log.info("Deleting expired offers");
        offerService.deleteBySecondDateAfter(LocalDate.now());
    }
}
