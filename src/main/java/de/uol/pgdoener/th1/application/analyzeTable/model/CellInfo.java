package de.uol.pgdoener.th1.application.analyzeTable.model;

/**
 * Represents a cell in the matrixInfo, including its column ID and whether it contains data.
 */
public record CellInfo(
        int rowIndex,
        int columnIndex,
        ValueType valueType
) {
}
