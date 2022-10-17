package com.godeltech.springgodelbot.model.repository;

import com.godeltech.springgodelbot.model.entity.DriverItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface DriverItemRepository extends JpaRepository<DriverItem, Long> {

    List<DriverItem> findByUserEntityId(Long id);

    @Query(value = "SELECT * FROM trip_offer o1 JOIN trip_offer_activity toa on o1.id = toa.trip_offer_id " +
            "JOIN activity a on a.id = toa.activity_id" +
            " WHERE o1.user_id IN (SELECT id from telegram_user WHERE is_valid = true)" +
            " AND ((o1.first_date <= :secondDate AND o1.second_date >= :firstDate) " +
            " OR (o1.second_date IS NULL AND o1.first_date >= :firstDate AND o1.first_date <= :secondDate)) " +
            "AND " +
            " o1.id IN (SELECT o2.id FROM trip_offer o2 JOIN trip_offer_city oc ON o2.id=" +
            "oc.trip_offer_id JOIN city c ON oc.city_id=c.id WHERE c.name IN :cities " +
            "GROUP BY o2.id " +
            "HAVING COUNT(o2.id)>= 2)", nativeQuery = true)
    Set<DriverItem> findByDatesAndCities(LocalDate secondDate, LocalDate firstDate,
                                         List<String> cities);

    @Query(value = "SELECT * FROM trip_offer o1 JOIN trip_offer_activity toa on o1.id = toa.trip_offer_id" +
            " JOIN activity a on a.id = toa.activity_id" +
            " WHERE o1.user_id IN (SELECT id from telegram_user WHERE is_valid = true)" +
            " AND ((o1.second_date is null AND o1.first_date = :firstDate) " +
            "OR (o1.second_date IS NOT NULL AND o1.first_date <= :firstDate AND o1.second_date >= :firstDate)) " +
            "AND " +
            " o1.id IN (SELECT o2.id FROM trip_offer o2 JOIN trip_offer_city oc ON o2.id=" +
            "oc.trip_offer_id JOIN city c ON oc.city_id=c.id WHERE c.name IN :cities " +
            "GROUP BY o2.id " +
            "HAVING COUNT(o2.id)>= 2)", nativeQuery = true)
    Set<DriverItem> findByFirstDateAndCities(LocalDate firstDate,
                                             List<String> cities);

    void deleteOffersBySecondDateBeforeAndSecondDateIsNotNull(LocalDate date);

    void deleteOffersByFirstDateBeforeAndSecondDateIsNull(LocalDate date);
}
