package com.godeltech.springgodelbot.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

@TestConfiguration
public class Config {
    @Bean
    public TestRestTemplate restTemplate() {
        return new TestRestTemplate();
    }

    @Bean
    public ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

}
