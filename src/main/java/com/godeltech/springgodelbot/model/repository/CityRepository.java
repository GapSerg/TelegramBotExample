package com.godeltech.springgodelbot.model.repository;

import com.godeltech.springgodelbot.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {
    Optional<City> findByName(String name);

    boolean existsByName(String name);

    @Query(value = "SELECT * FROM city c JOIN offer_city oc ON c.id=oc.city_id " +
            "WHERE oc.offer_id=:id " +
            "ORDER BY oc.id", nativeQuery = true)
    List<City> findCitiesByOfferId(Long id);
}
