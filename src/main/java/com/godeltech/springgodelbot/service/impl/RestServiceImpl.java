package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.service.RestService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Service
@RequiredArgsConstructor
public class RestServiceImpl implements RestService {
    private final RestTemplate restTemplate;

    @Override
    @SneakyThrows
    public void setWebHook(String webHookPath, String token) {
        restTemplate.exchange(RequestEntity
                .post(new URI("https://api.telegram.org/bot" + token + "/setWebHook?url=" + webHookPath))
                .build(), String.class);
    }
}
