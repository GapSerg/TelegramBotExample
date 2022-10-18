package com.godeltech.springgodelbot.model.entity;

import com.godeltech.springgodelbot.model.entity.enums.Activity;
import com.godeltech.springgodelbot.model.entity.types.PostgreSqlEnumType;
import com.vladmihalcea.hibernate.type.array.EnumArrayType;
import com.vladmihalcea.hibernate.type.array.ListArrayType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.*;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
@TypeDefs({
        @TypeDef(name = "postgreSqlEnumType", typeClass = PostgreSqlEnumType.class),
        @TypeDef(name = "postgreSqlListType", typeClass = ListArrayType.class),
        @TypeDef(name= "postgreSqlEnumArrayType",typeClass = EnumArrayType.class, parameters = {
                @Parameter(name = EnumArrayType.SQL_ARRAY_TYPE,
                        value = "activity_type")
        })
})
@AllArgsConstructor
@NoArgsConstructor
@DiscriminatorColumn(name = "request_type", columnDefinition = "type_of_Request")
public abstract class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Long offerId;

    @Column(columnDefinition = "text[]")
    @Type(type = "postgreSqlListType")
    private List<String> cities;
    @Column
    private LocalDate firstDate;
    @Column
    private LocalDate secondDate;
    @Column
    private Boolean needForDescription;
    @OneToOne(cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "token_id", referencedColumnName = "id")
    private Token token;
    @Column(columnDefinition = "activity_type[]")
    @Type(type = "postgreSqlEnumArrayType")
    private List<Activity> suitableActivities;

    @Enumerated(EnumType.STRING)
    @Column(columnDefinition = "activity_type")
    @Type(type = "postgreSqlEnumType")
    private Activity activity;
    @Column
    private String description;

    public Request(Activity activity) {
        this.activity = activity;
    }

}
