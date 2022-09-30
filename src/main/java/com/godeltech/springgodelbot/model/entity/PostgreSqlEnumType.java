package com.godeltech.springgodelbot.model.entity;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.type.EnumType;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.Optional;

public class PostgreSqlEnumType extends EnumType {
    @Override
    public void nullSafeSet(PreparedStatement st, Object value, int index,
                            SharedSessionContractImplementor session) throws HibernateException, SQLException {

        Object name = Optional.ofNullable(value)
                .map(val -> ((Enum) value).name())
                .orElseGet(null);

        st.setObject(index, name, Types.OTHER);
    }
}
