package com.godeltech.springgodelbot.scheduler.impl;

import com.godeltech.springgodelbot.scheduler.PingTaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

@Service
@Slf4j
public class PingTaskServiceImpl implements PingTaskService {
    @Value("${pingtask.url}")
    private String urlForPing;

    @Scheduled(fixedRateString = "${pingtask.period}")
    @Override
    public void pingMe() {
        try {
            URL url = new URL(urlForPing);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            log.info("Ping {}, OK: response code {}", url.getHost(), connection.getResponseCode());
            connection.disconnect();
        } catch (IOException e) {
            log.error("Ping job exited with error");
        }
    }
}
