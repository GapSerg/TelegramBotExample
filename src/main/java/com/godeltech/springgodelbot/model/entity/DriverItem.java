package com.godeltech.springgodelbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import javax.persistence.*;

import java.time.LocalDate;
import java.util.List;

import static javax.persistence.FetchType.EAGER;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DriverItem {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "driver_item_city",
            joinColumns = @JoinColumn(name = "driver_item_id"),
            inverseJoinColumns = @JoinColumn(name = "city_id")
    )
    @OnDelete(action = OnDeleteAction.CASCADE)
    private List<City> cities;

    @Column
    private LocalDate firstDate;

    @Column
    private LocalDate secondDate;

    @Column
    private String description;

    @ManyToMany(fetch = EAGER)
    @JoinTable(
            name = "driver_item_activity",
            joinColumns = @JoinColumn(name = "driver_item_id"),
            inverseJoinColumns = @JoinColumn(name = "activity_id")
    )
    private List<ActivityType> suitableActivities;
}
