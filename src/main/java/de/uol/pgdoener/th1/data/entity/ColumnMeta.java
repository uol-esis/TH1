package de.uol.pgdoener.th1.data.entity;

public record ColumnMeta(
        String columnName,
        String dataType,
        String isNullable,
        String constraintType
) {
}
