package com.godeltech.springgodelbot.model.entity;

import lombok.*;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Builder
public class Token {
    @Id
    private String id;
    @Column
    private Long userId;
    @Setter
    @Column
    private Integer messageId;
    @Column
    private Long chatId;
    @Column
    private Timestamp createdAt;
    @Column
    @Setter
    private boolean isReserved;
    @OneToOne(mappedBy = "token", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @ToString.Exclude
    private Request request;
}
