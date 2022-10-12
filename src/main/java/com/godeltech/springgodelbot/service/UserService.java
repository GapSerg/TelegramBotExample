package com.godeltech.springgodelbot.service;

import com.godeltech.springgodelbot.model.entity.UserEntity;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;

public interface UserService {


    UserEntity getById(Long userId, Message message, User user);

    UserEntity save(UserEntity userEntity);

    boolean existsById(Long id);

    void userAuthorization(User user, Message message, boolean isMessage);
}
