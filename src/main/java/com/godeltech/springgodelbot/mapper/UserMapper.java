package com.godeltech.springgodelbot.mapper;

import com.godeltech.springgodelbot.dto.UserDto;
import com.godeltech.springgodelbot.model.entity.UserEntity;
import org.apache.catalina.UserDatabase;
import org.telegram.telegrambots.meta.api.objects.User;

public interface UserMapper {
    User mapToUser(UserEntity userEntity);
    UserEntity mapToUserEntity(User user);
    UserEntity mapToUserEntity(UserDto userDto);

    UserDto mapToUserDto(UserEntity userEntity);
}
