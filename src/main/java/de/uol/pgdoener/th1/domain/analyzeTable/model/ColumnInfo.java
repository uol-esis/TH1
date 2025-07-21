package de.uol.pgdoener.th1.domain.analyzeTable.model;

import java.util.List;

/**
 * This summarizes a column of a table.
 *
 * @param columnIndex the index of the column
 * @param cellInfos   the cells of the table
 */
public record ColumnInfo(
        int columnIndex,
        List<CellInfo> cellInfos
) {
}
