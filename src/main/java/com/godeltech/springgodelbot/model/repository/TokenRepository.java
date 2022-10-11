package com.godeltech.springgodelbot.model.repository;

import com.godeltech.springgodelbot.model.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

public interface TokenRepository extends JpaRepository<Token,String> {
//    List<Token> findByUserIdAndReserved(Long userId, boolean isReserved);
    List<Token> findByUserIdAndIsReserved(Long userId, boolean isReserved);
    List<Token> findByCreatedAtBeforeAndMessageIdIsNull(Timestamp date);
    List<Token> findByCreatedAtBeforeAndMessageIdIsNotNull(Timestamp date);
}
