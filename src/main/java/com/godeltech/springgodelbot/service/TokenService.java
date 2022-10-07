package com.godeltech.springgodelbot.service;

import java.time.LocalDate;
import java.util.List;

public interface TokenService {
    void checkIncomeToken(String token, Long userId);

    void deleteToken(String token);

    List<String> findByUserId(Long id);

    String createToken();

    void deleteExpiredTokens(LocalDate date);
}
