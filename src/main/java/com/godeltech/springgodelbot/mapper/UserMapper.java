package com.godeltech.springgodelbot.mapper;
import com.godeltech.springgodelbot.model.entity.UserEntity;
import org.telegram.telegrambots.meta.api.objects.User;

public interface UserMapper {
    User mapToUser(UserEntity userEntity);

    UserEntity mapToUserEntity(User user);

}
