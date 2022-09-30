//package com.godeltech.springgodelbot.model.repository;
//
//import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.data.jpa.repository.Query;
//
//import java.time.LocalDate;
//import java.util.List;
//import java.util.Set;
//
//public interface PassengerRepository extends JpaRepository<Passenger,Long> {
//    @Query(value = "SELECT * FROM passenger p1 JOIN passenger_route pr ON p1.id=pr.passenger_id " +
//            "JOIN route r ON pr.route_id=r.id WHERE p1.first_date <= :secondDate AND p1.second_date >= :firstDate " +
//            "AND p1.id IN (SELECT p2.id FROM passenger p2 JOIN passenger_route pr2 ON p2.id = pr2.passenger_id" +
//            " JOIN route r2 ON pr2.route_id=r2.id WHERE r2.point_of_route IN :routes " +
//            "GROUP BY p2.id " +
//            "HAVING COUNT(p2.id)>= 2)",
//            nativeQuery = true)
//    Set<Passenger> findByFirstDateBeforeAndSecondDateAfterAndPlaceOfArrivalIdAndPlaceOfDepartureId
//            (LocalDate secondDate, LocalDate firstDate, List<String> routes);
//
//    List<Passenger> findByUserEntityId(Long id);
//}
