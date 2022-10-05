package com.godeltech.springgodelbot.service;

import java.util.List;

public interface MessageService {
    void checkIncomeToken(String token, Long userId);

    void deleteToken(String token);

    List<String> findByUserId(Long id);
}
