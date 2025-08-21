package de.uol.pgdoener.th1.domain.datatable.model;

import lombok.Getter;

@Getter
public enum SqlType {
    UNDEFINED("UNDEFINED"),
    INTEGER("INTEGER"),
    NUMERIC("NUMERIC"),
    BOOLEAN("BOOLEAN"),
    DATE("DATE"),
    TIMESTAMP("TIMESTAMP"),
    UUID("UUID"),
    TEXT("TEXT"),
    SERIAL_PRIMARY_KEY("SERIAL PRIMARY KEY");

    private final String sqlName;

    SqlType(String sqlName) {
        this.sqlName = sqlName;
    }

    public boolean isHigherPriorityThan(SqlType other) {
        return this.ordinal() > other.ordinal();
    }
}