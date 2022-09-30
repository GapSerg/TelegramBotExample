package com.godeltech.springgodelbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@TypeDef(name = "postgreSqlEnumType", typeClass = PostgreSqlEnumType.class)
@Builder
public class Offer {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserEntity userEntity;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "offer_city",
            joinColumns = @JoinColumn(name = "offer_id"),
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

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "activity_type")
    @Type(type = "postgreSqlEnumType")
    private Activity activity;

}
