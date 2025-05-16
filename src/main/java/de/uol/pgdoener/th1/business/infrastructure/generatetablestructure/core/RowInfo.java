package de.uol.pgdoener.th1.business.infrastructure.generatetablestructure.core;

import java.util.List;
import java.util.Map;

/**
 * Represents a row in the matrix, containing cell information and an optional header.
 */
public record RowInfo(
        int rowId,
        List<CellInfo> cellInfos
) {
}