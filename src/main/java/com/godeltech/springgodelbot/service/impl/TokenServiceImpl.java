package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.RepeatedTokenMessageException;
import com.godeltech.springgodelbot.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class TokenServiceImpl implements TokenService {
    private final Map<String, Long> incomeMessages;
    private final Map<String, LocalDate> reservedTokens;

    public TokenServiceImpl() {
        this.incomeMessages = new ConcurrentHashMap<>();
        reservedTokens = new ConcurrentHashMap<>();
    }

    @Override
    public void checkIncomeToken(String token, Long userId) {
        log.info("Income message with token :{}", token);
        if (incomeMessages.containsKey(token))
            throw new RepeatedTokenMessageException(token);
        incomeMessages.put(token, userId);
    }

    @Override
    public void deleteToken(String token) {
        log.info("Delete token from messages : {}", token);
        incomeMessages.remove(token);
        reservedTokens.remove(token);
    }

    @Override
    public List<String> findByUserId(Long id) {
        return incomeMessages.entrySet()
                .stream()
                .filter(entry -> id.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    @Override
    public String createToken() {
        log.info("Create new token");
        String token = UUID.randomUUID().toString().replaceAll("-", "").substring(10);
        if (reservedTokens.containsKey(token))
            token = createToken();

        reservedTokens.put(token, LocalDate.now());
        return token;

    }

    @Override
    public void deleteExpiredTokens(LocalDate date) {
        log.info("Remove expired token");
        reservedTokens.entrySet().stream()
                .filter(entry -> entry.getValue().isBefore(date))
                .forEach(entry -> reservedTokens.remove(entry.getKey()));
    }
}
