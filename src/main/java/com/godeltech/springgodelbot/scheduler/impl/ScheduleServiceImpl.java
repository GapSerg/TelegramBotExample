package com.godeltech.springgodelbot.scheduler.impl;

import com.godeltech.springgodelbot.scheduler.ScheduleService;
import com.godeltech.springgodelbot.service.OfferService;
import com.godeltech.springgodelbot.service.TokenService;
import com.godeltech.springgodelbot.service.impl.TudaSudaTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class ScheduleServiceImpl implements ScheduleService {

    private final OfferService offerService;
    private final TudaSudaTelegramBot tudaSudaTelegramBot;


    @Override
    @Scheduled(initialDelayString = "${schedule.start}", fixedDelayString = "${schedule.work}")
    public void deleteExpireOffers() {
        log.info("Deleting expired offers");
        LocalDate date = LocalDate.now();
        offerService.deleteBySecondDateAfter(date);
        offerService.deleteByFirstDateAfterWhereSecondDateIsNull(date);
    }
    @Override
    @Scheduled(initialDelayString = "${schedule.start}", fixedDelayString = "${schedule.work}")
    public void deleteExpireTokens() {
        log.info("Deleting expired offers");
        LocalDateTime localDateTime=  LocalDateTime.now().minusHours(16);
        tudaSudaTelegramBot.deleteExpiredTokens(localDateTime);
    }
}
