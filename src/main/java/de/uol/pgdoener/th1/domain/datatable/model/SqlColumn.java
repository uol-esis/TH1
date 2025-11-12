package de.uol.pgdoener.th1.domain.datatable.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SqlColumn {
    private final String name;
    private SqlType type;

    public SqlColumn(String name, SqlType type) {
        this.name = name;
        this.type = type;
    }

    public void mergeType(SqlType newType) {
        if (newType == null) return;
        if (this.type == null || newType.isHigherPriorityThan(this.type)) {
            this.type = newType;
        }
    }
}
