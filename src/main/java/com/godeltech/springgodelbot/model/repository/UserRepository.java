package com.godeltech.springgodelbot.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.godeltech.springgodelbot.model.entity.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<UserEntity,Long> {
    boolean existsByIdAndUserName(Long id, String username);

}
