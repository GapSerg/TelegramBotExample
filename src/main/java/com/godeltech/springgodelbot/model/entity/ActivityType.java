package com.godeltech.springgodelbot.model.entity;

import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.model.entity.types.PostgreSqlEnumType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;
import org.hibernate.annotations.TypeDefs;

import javax.persistence.*;

@Entity(name = "Activity")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
@TypeDefs(value = {
        @TypeDef(name = "postgreSqlEnumType", typeClass = PostgreSqlEnumType.class)
})
public class ActivityType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "activity_type")
    @Type(type = "postgreSqlEnumType")
    private Activity name;
}
