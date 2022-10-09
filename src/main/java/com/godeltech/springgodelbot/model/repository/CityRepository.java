package com.godeltech.springgodelbot.model.repository;

import com.godeltech.springgodelbot.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CityRepository extends JpaRepository<City, Integer> {

    boolean existsByName(String name);
}
