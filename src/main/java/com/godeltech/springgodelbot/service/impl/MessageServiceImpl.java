package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.RepeatedTokenMessageException;
import com.godeltech.springgodelbot.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MessageServiceImpl implements MessageService {
    private final Map<String,Long> incomeMessages;

    public MessageServiceImpl() {
        this.incomeMessages = new ConcurrentHashMap<>();
    }

    @Override
    public void checkIncomeToken(String token, Long userId) {
        log.info("Income message with token :{}", token);
        if (incomeMessages.containsKey(token))
            throw new RepeatedTokenMessageException(token);
        incomeMessages.put(token,userId);
    }

    @Override
    public void deleteToken(String token) {
        log.info("Delete token from messages : {}",token);
        incomeMessages.remove(token);
    }

    @Override
    public List<String> findByUserId(Long id) {
        return incomeMessages.entrySet()
                .stream()
                .filter(entry-> id.equals(entry.getValue()))
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }
}
