package de.uol.pgdoener.th1.data.entity;

import java.util.List;

public record TableMeta(
        String tableName,
        List<ColumnMeta> columns
) {
}
