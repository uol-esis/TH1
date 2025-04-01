package de.uol.pgdoener.th1.business.infrastructure.generatetablesructure.core;

import lombok.Getter;

@Getter
public class ColumnInfo {
    private final int columnId;
    private final boolean hasEntry;

    public ColumnInfo(int columnId, boolean hasEntry) {
        this.columnId = columnId;
        this.hasEntry = hasEntry;
    }

    public boolean hasEntry() {
        return hasEntry;
    }
}