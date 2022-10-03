package com.godeltech.springgodelbot.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class City {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    private Integer id;

    @Column
    private String name;


}
