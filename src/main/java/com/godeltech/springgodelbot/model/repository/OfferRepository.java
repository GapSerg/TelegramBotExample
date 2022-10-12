package com.godeltech.springgodelbot.model.repository;

import com.godeltech.springgodelbot.model.entity.Activity;
import com.godeltech.springgodelbot.model.entity.Offer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Repository
public interface OfferRepository extends JpaRepository<Offer, Long> {

    List<Offer> findByUserEntityIdAndActivity(Long id, Activity activity);

    @Query(value = "SELECT * FROM offer o1 WHERE o1.user_id IN (SELECT id from telegram_user WHERE is_valid = true)" +
            " AND ((o1.first_date <= :secondDate AND o1.second_date >= :firstDate) " +
            " OR (o1.second_date IS NULL AND o1.first_date >= :firstDate AND o1.first_date <= :secondDate)) "+
            "AND o1.activity = cast(:activity as activity_type) AND " +
            " o1.id IN (SELECT o2.id FROM offer o2 JOIN offer_city oc ON o2.id=" +
            "oc.offer_id JOIN city c ON oc.city_id=c.id WHERE c.name IN :cities " +
            "GROUP BY o2.id " +
            "HAVING COUNT(o2.id)>= 2)", nativeQuery = true)
    Set<Offer> findByDatesAndCitiesAndActivity(LocalDate secondDate, LocalDate firstDate, String activity,
                                               List<String> cities);
    @Query(value = "SELECT * FROM offer o1 WHERE o1.user_id IN (SELECT id from telegram_user WHERE is_valid = true)" +
            " AND ((o1.second_date is null AND o1.first_date = :firstDate) " +
            "OR (o1.second_date IS NOT NULL AND o1.first_date <= :firstDate AND o1.second_date >= :firstDate)) " +
            "AND o1.activity = cast(:activity as activity_type) AND " +
            " o1.id IN (SELECT o2.id FROM offer o2 JOIN offer_city oc ON o2.id=" +
            "oc.offer_id JOIN city c ON oc.city_id=c.id WHERE c.name IN :cities " +
            "GROUP BY o2.id " +
            "HAVING COUNT(o2.id)>= 2)", nativeQuery = true)
    Set<Offer> findByFirstDateAndCitiesAndActivity(LocalDate firstDate, String activity,
                                                   List<String> cities);

    void deleteOffersBySecondDateBeforeAndSecondDateIsNotNull(LocalDate date);

    void deleteOffersByFirstDateBeforeAndSecondDateIsNull(LocalDate date);
}
