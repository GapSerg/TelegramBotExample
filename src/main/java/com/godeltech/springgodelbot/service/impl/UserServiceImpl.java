package com.godeltech.springgodelbot.service.impl;

import com.godeltech.springgodelbot.exception.ResourceNotFoundException;
import com.godeltech.springgodelbot.model.entity.UserEntity;
import com.godeltech.springgodelbot.model.repository.UserRepository;
import com.godeltech.springgodelbot.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public UserEntity getById(Long userId, Message message, User user) {
        ;
        log.info("find user by id = {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(UserEntity.class, "id", userId, message,user ));
    }

    @Override
    @Transactional
    public UserEntity save(UserEntity userEntity, Message message) {
        log.info("Save a new user: {}", userEntity);
        return userRepository.save(userEntity);
    }

    @Override
    public boolean existsByIdAndUsername(Long id, String username) {
        log.info("Check existing of user by id: {}", id);
        return userRepository.existsByIdAndUserName(id, username);
    }
}
