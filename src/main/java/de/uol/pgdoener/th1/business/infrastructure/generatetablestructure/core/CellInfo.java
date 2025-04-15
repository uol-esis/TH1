package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

import lombok.Getter;

/**
 * Represents a cell in the matrixInfo, including its column ID and whether it contains data.
 */
@Getter
public class CellInfo {
    private final int columnId;
    private final boolean hasEntry;

    public CellInfo(int columnId, boolean hasEntry) {
        this.columnId = columnId;
        this.hasEntry = hasEntry;
    }

    public boolean hasEntry() {
        return hasEntry;
    }
}