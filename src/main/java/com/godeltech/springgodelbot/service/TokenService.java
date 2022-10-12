package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.Token;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.LocalDateTime;
import java.util.List;

public interface TokenService {
    Token checkIncomeToken(String token, Message message, User user);

    void deleteToken(String token, Message message, User user);

    List<String> findByUserId(Long id);

    Token createToken(Long userId, Integer messageId, Long chatId);
    Token createToken(Long userId,  Long chatId);

    List<Token> getUsableExpiredTokens(LocalDateTime date);

    Token getById(String token, Message message, User user);

    void deleteNonUsableExpiredTokens(LocalDateTime date);

    void deleteAll(List<Token> tokens);
}
