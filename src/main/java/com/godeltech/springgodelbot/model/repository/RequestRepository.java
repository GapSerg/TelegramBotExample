package com.godeltech.springgodelbot.model.repository;

import com.godeltech.springgodelbot.model.entity.Request;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    Optional<Request> findByTokenId(String tokenId);

    @Modifying
    @Query(value = "UPDATE request SET need_for_description=:status " +
            "FROM request r JOIN token t on t.id = r.token_id WHERE t.user_id=:userId", nativeQuery = true)
    void setNeedForDescriptionInRequestsWithUserId(boolean status, long userId);

    List<Request> findByTokenUserIdAndNeedForDescriptionTrue(Long userId);
}
