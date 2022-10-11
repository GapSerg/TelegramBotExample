package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.UserEntity;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface UserService {


    UserEntity getById(Long userId, Message message);

    UserEntity save(UserEntity userEntity, Message message);

    boolean existsByIdAndUsername(Long id, String username);
}
