package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

/**
 * Represents a cell in the matrixInfo, including its column ID and whether it contains data.
 */
public record CellInfo(
        int rowIndex,
        int columnId,
        String entry,
        ValueType valueType
) {
}
