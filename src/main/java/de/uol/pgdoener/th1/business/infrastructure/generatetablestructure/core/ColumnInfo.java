package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

import java.util.List;
import java.util.Map;

/**
 * This summarizes a column of a table.
 *
 * @param columnIndex the index of the column
 * @param cellInfos   the cells of the table
 * @param typeCounts  the count of cells for each type in this column
 */
public record ColumnInfo(
        int columnIndex,
        List<CellInfo> cellInfos,
        Map<ValueType, Integer> typeCounts
) {
}
