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

    @Query(value = "SELECT * FROM city c JOIN transfer_item_city oc ON c.id=oc.city_id " +
            "WHERE oc.transfer_item_id=:id " +
            "ORDER BY oc.id", nativeQuery = true)
    List<City> findCitiesForTransferItemByOfferId(Long id);

    @Query(value = "SELECT * FROM city c JOIN driver_item_city oc ON c.id=oc.city_id " +
            "WHERE oc.driver_item_id=:id " +
            "ORDER BY oc.id", nativeQuery = true)
    List<City> findCitiesForDriverItemByOfferId(Long id);
}
