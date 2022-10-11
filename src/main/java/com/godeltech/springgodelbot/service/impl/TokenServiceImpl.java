package com.godeltech.springgodelbot.service.impl;


import com.godeltech.springgodelbot.exception.RepeatedTokenMessageException;
import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.model.entity.Token;
import com.godeltech.springgodelbot.model.repository.TokenRepository;
import com.godeltech.springgodelbot.service.TokenService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@Slf4j
@Transactional(readOnly = true)
public class TokenServiceImpl implements TokenService {

    private final TokenRepository tokenRepository;


    public TokenServiceImpl(TokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    @Transactional
    public void checkIncomeToken(String id, Message message) {
        log.info("Income message with token :{}", id);
        Token token = getById(id, message);
        if (token.isReserved())
            throw new RepeatedTokenMessageException(id);
        token.setMessageId(message.getMessageId());
        token.setReserved(true);
        tokenRepository.save(token);
    }

    @Override
    @Transactional
    public void deleteToken(String id, Message message) {
        log.info("Delete token from messages : {}", id);
        Token token = getById(id, message);
        tokenRepository.delete(token);
    }

    @Override
    public List<String> findByUserId(Long userId) {
        log.info("Find tokens by userId :{}", userId);
        return tokenRepository.findByUserIdAndIsReserved(userId, true)
                .stream().map(Token::getId)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public Token createToken(Long userId, Integer messageId, Long chatId) {
        log.info("Create new token with userId :{}, messageId: {} and chatId :{}", userId, messageId, chatId);
        String tokenId = createTokenId();
        Token token = Token.builder()
                .id(tokenId)
                .userId(userId)
                .messageId(messageId)
                .chatId(chatId)
                .isReserved(false)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        return tokenRepository.save(token);
    }

    @Override
    @Transactional
    public Token createToken(Long userId, Long chatId) {
        log.info("Create new token with userId :{} and chatId :{}", userId, chatId);
        String tokenId = createTokenId();
        Token token = Token.builder()
                .id(tokenId)
                .userId(userId)
                .chatId(chatId)
                .isReserved(false)
                .createdAt(Timestamp.valueOf(LocalDateTime.now()))
                .build();
        return tokenRepository.save(token);
    }

    private String createTokenId() {
        String token = UUID.randomUUID().toString().replaceAll("-", "").substring(10);
        if (tokenRepository.findById(token).isPresent()) {
            token = createTokenId();
        }
        return token;
    }

    @Override
    public List<Token> getUsableExpiredTokens(LocalDateTime date) {
        log.info("Remove expired token");
        return tokenRepository.findByCreatedAtBeforeAndMessageIdIsNotNull(Timestamp.valueOf(date));
    }

    @Override
    public Token getById(String token, Message message) {
        return tokenRepository.findById(token)
                .orElseThrow(() -> new ResourceNotFoundException(Token.class, "id", token, message));
    }

    @Override
    public void deleteNonUsableExpiredTokens(LocalDateTime date) {
        log.info("Delete non usable expired tokens");
        tokenRepository.findByCreatedAtBeforeAndMessageIdIsNull(Timestamp.valueOf(date));
    }

    @Override
    public void deleteAll(List<Token> tokens) {
        log.info("Delete tokens : {}", tokens);
        tokenRepository.deleteAll(tokens);
    }
}
